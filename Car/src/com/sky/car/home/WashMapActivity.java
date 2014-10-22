package com.sky.car.home;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.framework.authorize.b;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.sky.base.BaseNormalActivity;
import com.sky.base.SHApplication;
import com.sky.car.R;
import com.sky.car.util.AudioRecorder;
import com.sky.car.util.ConfigDefinition;
import com.sky.car.util.ImageLoaderTask;
import com.sky.car.util.ImageTools;
import com.sky.car.widget.SHImageView;
import com.sky.widget.SHDialog;
import com.sky.widget.SHDialog.DialogItemClickListener;
import com.sky.widget.SHToast;

/**
 * 洗车地图
 * 
 * @author skypan
 * 
 */
public class WashMapActivity extends BaseNormalActivity implements ITaskListener {

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private TextView mTv_location;
	private Marker mMarkerShop;// 当前位置标记
	JSONArray jsonArray = null;
	private LinearLayout mLl_bottom_check, mLl_bottom_say;
	private int type;// 0：一键洗车 1：一键检测 2：紧急援助 3：保养维修 4：保险 5：专家解疑
	private SHPostTaskM shanghuTask;
	private Button mBtn_say;

	/**
	 * 照片
	 */
	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private static final int SCALE = 20;//照片缩小比例
	/**
	 * 录音
	 */
	private static final int MIN_RECORD_TIME = 1; // 最短录制时间，单位秒，0为无时间限制
	private static final int RECORD_OFF = 0; // 不在录音
	private static final int RECORD_ON = 1; // 正在录音
	private static final String RECORD_FILENAME = "record0033"; // 录音文件名
	private TextView mTvRecordDialogTxt;
	private ImageView mIvRecVolume;

	private Dialog mRecordDialog;
	private AudioRecorder mAudioRecorder;
	private MediaPlayer mMediaPlayer;
	private Thread mRecordThread;

	private int recordState = 0; // 录音状态
	private float recodeTime = 0.0f; // 录音时长
	private double voiceValue = 0.0; // 录音的音量值
	private boolean playState = false; // 录音的播放状态
	private boolean moveState = false; // 手指是否移动

	private float downY;
	private Dialog dialog;
	private ImageView iv_photo1,iv_photo2,iv_photo3;
	private RelativeLayout rl_photo1,rl_photo2,rl_photo3;
	private ArrayList<Bitmap> bitList = new ArrayList<Bitmap>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.fragment_wash_map);
		super.onCreate(savedInstanceState);
		type = getIntent().getIntExtra("type", 0);
		mLl_bottom_check = (LinearLayout) findViewById(R.id.ll_bottom_check);
		mLl_bottom_say = (LinearLayout) findViewById(R.id.ll_bottom_say);
		mBtn_say = (Button) findViewById(R.id.btn_say);
		switch (type) {
		case 0:
			mDetailTitlebar.setTitle("洗车地图");
			requestShop();
			// try {
			// jsonArray = new
			// JSONArray(getIntent().getStringExtra("jsonArray"));
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			break;
		case 1:
			mDetailTitlebar.setTitle("一键检测");
			mLl_bottom_check.setVisibility(View.VISIBLE);
			requestShop();
			break;
		case 2:
			mDetailTitlebar.setTitle("紧急援助");
			mLl_bottom_say.setVisibility(View.VISIBLE);
			requestShop();
			break;
		case 3:
			mDetailTitlebar.setTitle("保养维修");
			requestShop();
			break;
		case 4:
			mDetailTitlebar.setTitle("保险");
			requestShop();
			break;
		case 5:
			mDetailTitlebar.setTitle("专家解疑");
			break;
		}

		mTv_location = (TextView) findViewById(R.id.tv_location);
		mTv_location.setText("当前位置:" + SHApplication.getInstance().getAddress());
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		mBaiduMap.setMapStatus(msu);
		initOverlay();
		setListener();
	}

	private void requestShop() {
		shanghuTask = new SHPostTaskM();
		shanghuTask.setUrl(ConfigDefinition.URL + "shopquery.action");
		shanghuTask.setListener(this);
		shanghuTask.getTaskArgs().put("lat", SHApplication.getInstance().getLat());
		shanghuTask.getTaskArgs().put("lgt", SHApplication.getInstance().getLng());
		shanghuTask.getTaskArgs().put("maptype", 0);
		shanghuTask.getTaskArgs().put("opertype", 0);
		shanghuTask.getTaskArgs().put("pageno", 1);
		shanghuTask.getTaskArgs().put("pagesize", 100);
		shanghuTask.getTaskArgs().put("keyname", "");
		switch (type) {
		case 0:
			shanghuTask.getTaskArgs().put("ishaswash", 1);
			shanghuTask.getTaskArgs().put("ishascheck", 0);
			shanghuTask.getTaskArgs().put("ishasmaintainance", 0);
			shanghuTask.getTaskArgs().put("ishasurgentrescure", 0);
			shanghuTask.getTaskArgs().put("ishassellinsurance", 0);
			break;
		case 1:
			shanghuTask.getTaskArgs().put("ishaswash", 0);
			shanghuTask.getTaskArgs().put("ishascheck", 1);
			shanghuTask.getTaskArgs().put("ishasmaintainance", 0);
			shanghuTask.getTaskArgs().put("ishasurgentrescure", 0);
			shanghuTask.getTaskArgs().put("ishassellinsurance", 0);
			break;
		case 2:
			shanghuTask.getTaskArgs().put("ishaswash", 0);
			shanghuTask.getTaskArgs().put("ishascheck", 0);
			shanghuTask.getTaskArgs().put("ishasmaintainance", 0);
			shanghuTask.getTaskArgs().put("ishasurgentrescure", 1);
			shanghuTask.getTaskArgs().put("ishassellinsurance", 0);
			break;
		case 3:
			shanghuTask.getTaskArgs().put("ishaswash", 0);
			shanghuTask.getTaskArgs().put("ishascheck", 0);
			shanghuTask.getTaskArgs().put("ishasmaintainance", 1);
			shanghuTask.getTaskArgs().put("ishasurgentrescure", 0);
			shanghuTask.getTaskArgs().put("ishassellinsurance", 0);
			break;
		case 4:
			shanghuTask.getTaskArgs().put("ishaswash", 0);
			shanghuTask.getTaskArgs().put("ishascheck", 0);
			shanghuTask.getTaskArgs().put("ishasmaintainance", 0);
			shanghuTask.getTaskArgs().put("ishasurgentrescure", 0);
			shanghuTask.getTaskArgs().put("ishassellinsurance", 1);
			break;
		}
		shanghuTask.start();
	}

	// @Override
	// public void onViewCreated(View view, Bundle savedInstanceState) {
	// // TODO Auto-generated method stub
	// super.onViewCreated(view, savedInstanceState);
	// mDetailTitlebar.setTitle("洗车地图");
	//
	// }

	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState) {
	// // TODO Auto-generated method stub
	// View view = inflater.inflate(R.layout.fragment_wash_map, container,
	// false);
	// return view;
	// }

	public void initOverlay() {
		// add marker overlay
		LatLng currentLatLng = new LatLng(SHApplication.getInstance().getLat(), SHApplication.getInstance().getLng());
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(currentLatLng);
		mBaiduMap.animateMapStatus(u);// 定位到当前位置
		OverlayOptions ooCurrent = new MarkerOptions().position(currentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding)).zIndex(5);
		mBaiduMap.addOverlay(ooCurrent);// 当前位置图标
		addOverlay();
	}

	private void addOverlay() {
		if (jsonArray != null && jsonArray.length() > 0) {
			// InfoWindow mInfoWindow;
			OverlayOptions ooShop;
			View view = LayoutInflater.from(this).inflate(R.layout.popup_map, null);
			TextView tv_shop_name = (TextView) view.findViewById(R.id.tv_shop_name);
			TextView tv_shop_address = (TextView) view.findViewById(R.id.tv_shop_address);
			RatingBar rating = (RatingBar) view.findViewById(R.id.rating);

			for (int i = 0; i < jsonArray.length(); i++) {
				SHImageView iv_shop = (SHImageView) view.findViewById(R.id.iv_shop);
				LatLng point = null;
				try {
					point = new LatLng(jsonArray.getJSONObject(i).optJSONObject("baidulatitude").optDouble("lat"), jsonArray.getJSONObject(i).optJSONObject("baidulatitude").optDouble("lgt"));
					tv_shop_name.setText(jsonArray.getJSONObject(i).optString("shopname"));
					tv_shop_address.setText(jsonArray.getJSONObject(i).optString("shopaddress"));
					rating.setRating(Float.valueOf(jsonArray.getJSONObject(i).optString("shopscore").toString()));
					// iv_shop.setNewImgForImageSrc(true);
					// iv_shop.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.dashboard_fault));
//					iv_shop.setURL("http://www.baidu.com/img/bd_logo1.png");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ooShop = new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromView(view)).zIndex(i);
				mMarkerShop = (Marker) (mBaiduMap.addOverlay(ooShop));
			}
		}
	}

	private void setListener() {
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WashMapActivity.this, ShopDetailActivity.class);
				try {
					intent.putExtra("shopid", jsonArray.getJSONObject(marker.getZIndex()).optString("shopid"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startActivity(intent);
				return true;
			}
		});

		mBtn_say.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: // 按下按钮
					if (recordState != RECORD_ON) {
						downY = event.getY();
						deleteOldFile();
						mAudioRecorder = new AudioRecorder(RECORD_FILENAME);
						recordState = RECORD_ON;
						try {
							mAudioRecorder.start();
							recordTimethread();
							showVoiceDialog(0);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				case MotionEvent.ACTION_MOVE: // 滑动手指
					float moveY = event.getY();
					if (downY - moveY > 50) {
						moveState = true;
						showVoiceDialog(1);
					}
					if (downY - moveY < 20) {
						moveState = false;
						showVoiceDialog(0);
					}
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP: // 松开手指
					if (recordState == RECORD_ON) {
						recordState = RECORD_OFF;
						if (mRecordDialog.isShowing()) {
							mRecordDialog.dismiss();
						}
						try {
							mAudioRecorder.stop();
							mRecordThread.interrupt();
							voiceValue = 0.0;
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (!moveState) {
							if (recodeTime < MIN_RECORD_TIME) {
								// mRl_voice.setVisibility(View.GONE);
								deleteOldFile();
								SHToast.showToast(WashMapActivity.this, "录音时间太短", SHToast.LENGTH_SHORT);
							} else {
								// 录音完成之后...
								showDialog();
								// mRl_voice.setVisibility(View.VISIBLE);
								// mIv_play.setVisibility(View.VISIBLE);
								// mTv_playing.setVisibility(View.INVISIBLE);
								// mTv_time.setText(((int) recodeTime) + "\"");
								// new Handler().post(new Runnable() {
								//
								// @Override
								// public void run() {
								// // TODO Auto-generated method stub
								// ((ScrollView)
								// findViewById(R.id.sv)).fullScroll(ScrollView.FOCUS_DOWN);
								// }
								// });
							}
						} else {
							deleteOldFile();
						}
						moveState = false;
					}
					break;
				}
				return false;
			}
		});
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mMapView.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		mMapView.onPause();
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		mMapView.onResume();
		super.onResume();
	}

	// 删除老文件
	void deleteOldFile() {
		System.out.println(Environment.getExternalStorageDirectory());
		File file = new File(Environment.getExternalStorageDirectory(), "/car/voiceRecord/" + RECORD_FILENAME + ".amr");
		if (file.exists()) {
			System.out.println("exist........");
			file.getAbsoluteFile().delete();
		}
	}

	// 录音时显示Dialog
	void showVoiceDialog(int flag) {
		if (mRecordDialog == null) {
			mRecordDialog = new Dialog(WashMapActivity.this, R.style.DialogStyle);
			mRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mRecordDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			mRecordDialog.setContentView(R.layout.dialog_record);
			mIvRecVolume = (ImageView) mRecordDialog.findViewById(R.id.record_dialog_img);
			mTvRecordDialogTxt = (TextView) mRecordDialog.findViewById(R.id.record_dialog_txt);
		}
		switch (flag) {
		case 1:
			mIvRecVolume.setImageResource(R.drawable.img_talk);
			mTvRecordDialogTxt.setText("松开手指可取消录音");
			break;

		default:
			mIvRecVolume.setImageResource(R.drawable.img_talk);
			mTvRecordDialogTxt.setText("向上滑动可取消录音");
			break;
		}
		mTvRecordDialogTxt.setTextSize(14);
		mRecordDialog.show();
	}

	// 获取文件手机路径
	private String getAmrPath() {
		File file = new File(Environment.getExternalStorageDirectory(), "/car/voiceRecord/" + RECORD_FILENAME + ".amr");
		return file.getAbsolutePath();
	}

	// 录音计时线程
	void recordTimethread() {
		mRecordThread = new Thread(recordThread);
		mRecordThread.start();
	}

	// 录音线程
	private Runnable recordThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0.0f;
			while (recordState == RECORD_ON) {
				// 限制录音时长
				// if (recodeTime >= MAX_RECORD_TIME && MAX_RECORD_TIME != 0) {
				// imgHandle.sendEmptyMessage(0);
				// } else
				{
					try {
						Thread.sleep(150);
						recodeTime += 0.15;
						// 获取音量，更新dialog
						if (!moveState) {
							voiceValue = mAudioRecorder.getAmplitude();
							// recordHandler.sendEmptyMessage(1);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	private void showDialog() {
		dialog = new Dialog(this, R.style.OptionDialog);
		dialog.setContentView(R.layout.dialog_map);
		setDialogListener();
		/**
		 * 设置位置
		 */
		WindowManager windowManager = (WindowManager) this.getSystemService(this.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (display.getWidth()); // 设置宽度
		lp.x = 0;
		lp.y = display.getHeight();
		dialog.getWindow().setAttributes(lp);
		dialog.show();
	}

	private void setDialogListener() {
		if (dialog != null) {
			iv_photo1 = (ImageView) dialog.findViewById(R.id.iv_photo1);
			iv_photo2 = (ImageView) dialog.findViewById(R.id.iv_photo2);
			iv_photo3 = (ImageView) dialog.findViewById(R.id.iv_photo3);
			ImageView iv_delete1 = (ImageView) dialog.findViewById(R.id.iv_delete1);
			ImageView iv_delete2 = (ImageView) dialog.findViewById(R.id.iv_delete2);
			ImageView iv_delete3 = (ImageView) dialog.findViewById(R.id.iv_delete3);
			rl_photo1 = (RelativeLayout) dialog.findViewById(R.id.rl_photo1);
			rl_photo2 = (RelativeLayout) dialog.findViewById(R.id.rl_photo2);
			rl_photo3 = (RelativeLayout) dialog.findViewById(R.id.rl_photo3);
			iv_delete1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					bitList.remove(0);
					rl_photo1.setVisibility(View.GONE);
				}
			});
			TextView tv_time = (TextView) dialog.findViewById(R.id.tv_time);
			tv_time.setText(((int) recodeTime)+"\"");
			RelativeLayout rl_play = (RelativeLayout) dialog.findViewById(R.id.rl_play);
			rl_play.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!playState) {
						mMediaPlayer = new MediaPlayer();
						try {
							mMediaPlayer.setDataSource(getAmrPath());
							mMediaPlayer.prepare();
							playState = true;
							mMediaPlayer.start();

							// 设置播放结束时监听
							mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer mp) {
									if (playState) {
										playState = false;
									}
								}
							});
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}
			});
			ImageView iv_add_image = (ImageView) dialog.findViewById(R.id.iv_add_image);
			iv_add_image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(bitList.size() == 3){
						SHToast.showToast(WashMapActivity.this, "最多上传3张照片", 1000);
						return;
					}
					final String[] items = new String[]{"拍照","相册"};
					SHDialog.showActionSheet(WashMapActivity.this, "选择照片方式", items, new DialogItemClickListener() {
						
						@Override
						public void onSelected(String result) {
							// TODO Auto-generated method stub
							if(items[0].equals(result)){
								
							}else{
								Intent it_local = new Intent(Intent.ACTION_GET_CONTENT, null);
								it_local.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
								startActivityForResult(it_local, PHOTOZOOM);
							}
						}
					});
				}
			});
			Button btn_submit = (Button) dialog.findViewById(R.id.btn_submit);
			btn_submit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					SHDialog.showOneKeyDialog(WashMapActivity.this, "需求发布成功", null);
				}
			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(data != null){
			switch(requestCode){
			case PHOTOZOOM:
//				Bitmap bitmap = null;
//				try {
//					bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				if(iv_photo1 != null){
////					Bitmap b = comp(bitmap);
//					bitmap = ThumbnailUtils.extractThumbnail(comp(bitmap), 80, 108);
//					bitmap.recycle();
////					bitmap = bitmap.createScaledBitmap(bitmap, 80, 100, true);
////					bitmap = getBitmapByBytes(bitmap);
////					bitmap.compress(Bitmap.CompressFormat.PNG, 30, stream);// (0 - 100)压缩文件 
//					iv_photo1.setImageBitmap(bitmap);
//				}
				ContentResolver resolver = getContentResolver();
				//照片的原始资源地址
				Uri originalUri = data.getData(); 
//				new ImageLoaderTask(this,iv_photo1).execute(originalUri);
	            try {
	            	//使用ContentProvider通过URI获取原始图片
					Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
					
					if (photo != null) {
						//为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
						Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
						//释放原始图片占用的内存，防止out of memory异常发生
						photo.recycle();
						rl_photo1.setVisibility(View.VISIBLE);
						iv_photo1.setImageBitmap(smallBitmap);
						bitList.add(smallBitmap);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}  
//				startPhotoZoom(data.getData());
				break;
			case PHOTORESOULT:
				Bitmap photo = data.getExtras().getParcelable("data");
				iv_photo1.setImageBitmap(photo);
				break;
			}
		}
	}

	/**
	 * 缩放裁剪图片
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 10);
		intent.putExtra("aspectY", 10);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 70);
		intent.putExtra("outputY", 70);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, PHOTORESOULT);
	}
	
//	private Bitmap comp(Bitmap image) {  
//	      
//	    ByteArrayOutputStream baos = new ByteArrayOutputStream();         
//	    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
//	    if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出    
////	    	System.out.println("M:"+ baos.toByteArray().length/1024);
//	        baos.reset();//重置baos即清空baos  
//	        image.compress(Bitmap.CompressFormat.JPEG, 40, baos);//这里压缩50%，把压缩后的数据存放到baos中  
//	    }  
//	    if(!image.isRecycled()){
//	    	image.recycle();
//	    }
//	    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
//	    BitmapFactory.Options newOpts = new BitmapFactory.Options();  
//	    //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
//	    newOpts.inJustDecodeBounds = true;  
//	    newOpts.inPreferredConfig = Config.RGB_565;
//	    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
//	    newOpts.inJustDecodeBounds = false;  
//	    int w = newOpts.outWidth;  
//	    int h = newOpts.outHeight;  
//	    //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
//	    float hh = 800f;//这里设置高度为800f  
//	    float ww = 480f;//这里设置宽度为480f  
//	    //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
//	    int be = 1;//be=1表示不缩放  
//	    if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
//	        be = (int) (newOpts.outWidth / ww);  
//	    } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
//	        be = (int) (newOpts.outHeight / hh);  
//	    }  
//	    if (be <= 0)  
//	        be = 1;  
//	    newOpts.inSampleSize = be;//设置缩放比例  
//	    //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
//	    isBm = new ByteArrayInputStream(baos.toByteArray());  
//	    bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
////	    return compressImage(bitmap);//压缩好比例大小后再进行质量压缩  
//	    return bitmap;
//	}
	
//    private Bitmap compressImage(Bitmap image) {  
//        
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
//        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
//        int options = 90;  
//        
//        while ( baos.toByteArray().length / 1024>100 && options > 10) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
////        	System.out.println(baos.toByteArray().length / 1024);
//            baos.reset();//重置baos即清空baos  
//            options -= 10;//每次都减少10  
//            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
//        }  
//        if(!image.isRecycled()){
//	    	image.recycle();
//	    }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
//        return bitmap;  
//    }  
	
//    public static Bitmap getBitmapByBytes(Bitmap bit){  
//    	 ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    	 bit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//         byte[] bytes = baos.toByteArray();
//        //对于图片的二次采样,主要得到图片的宽与高  
//        int width = 0;  
//        int height = 0;  
//        int sampleSize = 1; //默认缩放为1  
//        BitmapFactory.Options options = new BitmapFactory.Options();  
//        options.inJustDecodeBounds = true;  //仅仅解码边缘区域  
//        //如果指定了inJustDecodeBounds，decodeByteArray将返回为空  
//        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);  
//        //得到宽与高  
//        height = options.outHeight;  
//        width = options.outWidth;  
//      
//        //图片实际的宽与高，根据默认最大大小值，得到图片实际的缩放比例  
//        while ((height / sampleSize > 300)  
//                || (width / sampleSize > 150)) {  
//            sampleSize *= 2;  
//        }  
//      
//        //不再只加载图片实际边缘  
//        options.inJustDecodeBounds = false;  
//        //并且制定缩放比例  
//        options.inSampleSize = sampleSize;  
//        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);  
//    }  
    
	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		jsonArray = ((JSONObject) task.getResult()).getJSONArray("nearshops");
		addOverlay();
	}

	@Override
	public void onTaskFailed(SHTask task) {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		SHDialog.showOneKeyDialog(this, task.getRespInfo().getMessage(), null);
	}

	@Override
	public void onTaskUpdateProgress(SHTask task, int count, int total) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskTry(SHTask task) {
		// TODO Auto-generated method stub

	}

}
