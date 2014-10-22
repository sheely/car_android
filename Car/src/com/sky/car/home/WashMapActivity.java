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
 * ϴ����ͼ
 * 
 * @author skypan
 * 
 */
public class WashMapActivity extends BaseNormalActivity implements ITaskListener {

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private TextView mTv_location;
	private Marker mMarkerShop;// ��ǰλ�ñ��
	JSONArray jsonArray = null;
	private LinearLayout mLl_bottom_check, mLl_bottom_say;
	private int type;// 0��һ��ϴ�� 1��һ����� 2������Ԯ�� 3������ά�� 4������ 5��ר�ҽ���
	private SHPostTaskM shanghuTask;
	private Button mBtn_say;

	/**
	 * ��Ƭ
	 */
	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;// ����
	public static final int PHOTOZOOM = 2; // ����
	public static final int PHOTORESOULT = 3;// ���
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private static final int SCALE = 20;//��Ƭ��С����
	/**
	 * ¼��
	 */
	private static final int MIN_RECORD_TIME = 1; // ���¼��ʱ�䣬��λ�룬0Ϊ��ʱ������
	private static final int RECORD_OFF = 0; // ����¼��
	private static final int RECORD_ON = 1; // ����¼��
	private static final String RECORD_FILENAME = "record0033"; // ¼���ļ���
	private TextView mTvRecordDialogTxt;
	private ImageView mIvRecVolume;

	private Dialog mRecordDialog;
	private AudioRecorder mAudioRecorder;
	private MediaPlayer mMediaPlayer;
	private Thread mRecordThread;

	private int recordState = 0; // ¼��״̬
	private float recodeTime = 0.0f; // ¼��ʱ��
	private double voiceValue = 0.0; // ¼��������ֵ
	private boolean playState = false; // ¼���Ĳ���״̬
	private boolean moveState = false; // ��ָ�Ƿ��ƶ�

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
			mDetailTitlebar.setTitle("ϴ����ͼ");
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
			mDetailTitlebar.setTitle("һ�����");
			mLl_bottom_check.setVisibility(View.VISIBLE);
			requestShop();
			break;
		case 2:
			mDetailTitlebar.setTitle("����Ԯ��");
			mLl_bottom_say.setVisibility(View.VISIBLE);
			requestShop();
			break;
		case 3:
			mDetailTitlebar.setTitle("����ά��");
			requestShop();
			break;
		case 4:
			mDetailTitlebar.setTitle("����");
			requestShop();
			break;
		case 5:
			mDetailTitlebar.setTitle("ר�ҽ���");
			break;
		}

		mTv_location = (TextView) findViewById(R.id.tv_location);
		mTv_location.setText("��ǰλ��:" + SHApplication.getInstance().getAddress());
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
	// mDetailTitlebar.setTitle("ϴ����ͼ");
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
		mBaiduMap.animateMapStatus(u);// ��λ����ǰλ��
		OverlayOptions ooCurrent = new MarkerOptions().position(currentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding)).zIndex(5);
		mBaiduMap.addOverlay(ooCurrent);// ��ǰλ��ͼ��
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
				case MotionEvent.ACTION_DOWN: // ���°�ť
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
				case MotionEvent.ACTION_MOVE: // ������ָ
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
				case MotionEvent.ACTION_UP: // �ɿ���ָ
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
								SHToast.showToast(WashMapActivity.this, "¼��ʱ��̫��", SHToast.LENGTH_SHORT);
							} else {
								// ¼�����֮��...
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

	// ɾ�����ļ�
	void deleteOldFile() {
		System.out.println(Environment.getExternalStorageDirectory());
		File file = new File(Environment.getExternalStorageDirectory(), "/car/voiceRecord/" + RECORD_FILENAME + ".amr");
		if (file.exists()) {
			System.out.println("exist........");
			file.getAbsoluteFile().delete();
		}
	}

	// ¼��ʱ��ʾDialog
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
			mTvRecordDialogTxt.setText("�ɿ���ָ��ȡ��¼��");
			break;

		default:
			mIvRecVolume.setImageResource(R.drawable.img_talk);
			mTvRecordDialogTxt.setText("���ϻ�����ȡ��¼��");
			break;
		}
		mTvRecordDialogTxt.setTextSize(14);
		mRecordDialog.show();
	}

	// ��ȡ�ļ��ֻ�·��
	private String getAmrPath() {
		File file = new File(Environment.getExternalStorageDirectory(), "/car/voiceRecord/" + RECORD_FILENAME + ".amr");
		return file.getAbsolutePath();
	}

	// ¼����ʱ�߳�
	void recordTimethread() {
		mRecordThread = new Thread(recordThread);
		mRecordThread.start();
	}

	// ¼���߳�
	private Runnable recordThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0.0f;
			while (recordState == RECORD_ON) {
				// ����¼��ʱ��
				// if (recodeTime >= MAX_RECORD_TIME && MAX_RECORD_TIME != 0) {
				// imgHandle.sendEmptyMessage(0);
				// } else
				{
					try {
						Thread.sleep(150);
						recodeTime += 0.15;
						// ��ȡ����������dialog
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
		 * ����λ��
		 */
		WindowManager windowManager = (WindowManager) this.getSystemService(this.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (display.getWidth()); // ���ÿ��
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

							// ���ò��Ž���ʱ����
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
						SHToast.showToast(WashMapActivity.this, "����ϴ�3����Ƭ", 1000);
						return;
					}
					final String[] items = new String[]{"����","���"};
					SHDialog.showActionSheet(WashMapActivity.this, "ѡ����Ƭ��ʽ", items, new DialogItemClickListener() {
						
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
					SHDialog.showOneKeyDialog(WashMapActivity.this, "���󷢲��ɹ�", null);
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
////					bitmap.compress(Bitmap.CompressFormat.PNG, 30, stream);// (0 - 100)ѹ���ļ� 
//					iv_photo1.setImageBitmap(bitmap);
//				}
				ContentResolver resolver = getContentResolver();
				//��Ƭ��ԭʼ��Դ��ַ
				Uri originalUri = data.getData(); 
//				new ImageLoaderTask(this,iv_photo1).execute(originalUri);
	            try {
	            	//ʹ��ContentProviderͨ��URI��ȡԭʼͼƬ
					Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
					
					if (photo != null) {
						//Ϊ��ֹԭʼͼƬ�������ڴ��������������Сԭͼ��ʾ��Ȼ���ͷ�ԭʼBitmapռ�õ��ڴ�
						Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
						//�ͷ�ԭʼͼƬռ�õ��ڴ棬��ֹout of memory�쳣����
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
	 * ���Ųü�ͼƬ
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 10);
		intent.putExtra("aspectY", 10);
		// outputX outputY �ǲü�ͼƬ���
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
//	    if( baos.toByteArray().length / 1024>1024) {//�ж����ͼƬ����1M,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���    
////	    	System.out.println("M:"+ baos.toByteArray().length/1024);
//	        baos.reset();//����baos�����baos  
//	        image.compress(Bitmap.CompressFormat.JPEG, 40, baos);//����ѹ��50%����ѹ��������ݴ�ŵ�baos��  
//	    }  
//	    if(!image.isRecycled()){
//	    	image.recycle();
//	    }
//	    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
//	    BitmapFactory.Options newOpts = new BitmapFactory.Options();  
//	    //��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��  
//	    newOpts.inJustDecodeBounds = true;  
//	    newOpts.inPreferredConfig = Config.RGB_565;
//	    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
//	    newOpts.inJustDecodeBounds = false;  
//	    int w = newOpts.outWidth;  
//	    int h = newOpts.outHeight;  
//	    //���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ  
//	    float hh = 800f;//�������ø߶�Ϊ800f  
//	    float ww = 480f;//�������ÿ��Ϊ480f  
//	    //���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��  
//	    int be = 1;//be=1��ʾ������  
//	    if (w > h && w > ww) {//�����ȴ�Ļ����ݿ�ȹ̶���С����  
//	        be = (int) (newOpts.outWidth / ww);  
//	    } else if (w < h && h > hh) {//����߶ȸߵĻ����ݿ�ȹ̶���С����  
//	        be = (int) (newOpts.outHeight / hh);  
//	    }  
//	    if (be <= 0)  
//	        be = 1;  
//	    newOpts.inSampleSize = be;//�������ű���  
//	    //���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��  
//	    isBm = new ByteArrayInputStream(baos.toByteArray());  
//	    bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
////	    return compressImage(bitmap);//ѹ���ñ�����С���ٽ�������ѹ��  
//	    return bitmap;
//	}
	
//    private Bitmap compressImage(Bitmap image) {  
//        
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
//        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��  
//        int options = 90;  
//        
//        while ( baos.toByteArray().length / 1024>100 && options > 10) {  //ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��         
////        	System.out.println(baos.toByteArray().length / 1024);
//            baos.reset();//����baos�����baos  
//            options -= 10;//ÿ�ζ�����10  
//            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��  
//        }  
//        if(!image.isRecycled()){
//	    	image.recycle();
//	    }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��  
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ  
//        return bitmap;  
//    }  
	
//    public static Bitmap getBitmapByBytes(Bitmap bit){  
//    	 ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    	 bit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//         byte[] bytes = baos.toByteArray();
//        //����ͼƬ�Ķ��β���,��Ҫ�õ�ͼƬ�Ŀ����  
//        int width = 0;  
//        int height = 0;  
//        int sampleSize = 1; //Ĭ������Ϊ1  
//        BitmapFactory.Options options = new BitmapFactory.Options();  
//        options.inJustDecodeBounds = true;  //���������Ե����  
//        //���ָ����inJustDecodeBounds��decodeByteArray������Ϊ��  
//        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);  
//        //�õ������  
//        height = options.outHeight;  
//        width = options.outWidth;  
//      
//        //ͼƬʵ�ʵĿ���ߣ�����Ĭ������Сֵ���õ�ͼƬʵ�ʵ����ű���  
//        while ((height / sampleSize > 300)  
//                || (width / sampleSize > 150)) {  
//            sampleSize *= 2;  
//        }  
//      
//        //����ֻ����ͼƬʵ�ʱ�Ե  
//        options.inJustDecodeBounds = false;  
//        //�����ƶ����ű���  
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
