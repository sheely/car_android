package com.sky.car.home;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.next.util.SHEnvironment;
import com.sky.base.DetailTitlebar;
import com.sky.car.R;
import com.sky.car.util.AudioRecorder;
import com.sky.car.util.ConfigDefinition;
import com.sky.car.util.SHLocationManager;
import com.sky.car.widget.SHImageView;
import com.sky.widget.SHDialog;
import com.sky.widget.SHToast;

/**
 * ϴ����ͼ
 * 
 * @author skypan
 * 
 */
public class WashMapActivity extends FragmentActivity implements ITaskListener {

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private TextView mTv_location;
	private Marker mMarkerShop;// ��ǰλ�ñ��
	private Marker mCenterMarker;
	JSONArray jsonArray = null;
	private LinearLayout mLl_bottom_check, mLl_bottom_say;
	private ImageView iv_zhuanjia;
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
	private static final int SCALE = 6;//��Ƭ��С����
	/**
	 * ¼��
	 */
	private static final int MIN_RECORD_TIME = 1; // ���¼��ʱ�䣬��λ�룬0Ϊ��ʱ������
	private static final int RECORD_OFF = 0; // ����¼��
	private static final int RECORD_ON = 1; // ����¼��
	private static final String RECORD_FILENAME = "record0033"; // ¼���ļ���
	private TextView mTvRecordDialogTxt;
	private static ImageView mIvRecVolume;
	private ImageView mIv_voice_Image;

	private Dialog mRecordDialog;
	private AudioRecorder mAudioRecorder;
	private MediaPlayer mMediaPlayer;
	private Thread mRecordThread;

	private int recordState = 0; // ¼��״̬
	public float recodeTime = 0.0f; // ¼��ʱ��
	private double voiceValue = 0.0; // ¼��������ֵ
	private boolean playState = false; // ¼���Ĳ���״̬
	private boolean moveState = false; // ��ָ�Ƿ��ƶ�

	private float downY;
	private DetailTitlebar mDetailTitlebar;
	private ReleaseFragment releaseFragment;
	OverlayOptions ooCurrent;
	private Handler volumeHandler;
	private Button mBtn_keyboard;
	private EditText mEt_content;
	private boolean flag = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_wash_map);
		volumeHandler = new ShowVolumeHandler();
		mDetailTitlebar = (DetailTitlebar) findViewById(R.id.detailTitlebar);
		mDetailTitlebar.setLeftButton(R.drawable.ic_back, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		type = getIntent().getIntExtra("type", 0);
		mDetailTitlebar.setRightButton(R.drawable.ic_list, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(type == 0){
					finish();
				}else{
					Intent intent = new Intent(WashMapActivity.this,OneKeyWashActivity.class);
					intent.putExtra("type", type);
					startActivity(intent);
				}
			}
		});
//		ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
//		rl_photo1 = (RelativeLayout) findViewById(R.id.rl_photo1);
//		iv_photo1 = (ImageView) findViewById(R.id.iv_photo1);
//		iv_add_photo = (ImageView) findViewById(R.id.iv_add_image);
//		iv_add_photo.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent it_local = new Intent(Intent.ACTION_GET_CONTENT, null);
//				it_local.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//				startActivityForResult(it_local, 0);
//			}
//		});
		iv_zhuanjia = (ImageView) findViewById(R.id.iv_zhuanjia);
		mLl_bottom_check = (LinearLayout) findViewById(R.id.ll_bottom_check);
		mLl_bottom_say = (LinearLayout) findViewById(R.id.ll_bottom_say);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mEt_content = (EditText) findViewById(R.id.et_content);
		mEt_content.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (v == mEt_content && keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_UP) {
					if(mEt_content.getText().toString().trim().length() <= 0){
						SHToast.showToast(WashMapActivity.this, "�������ݲ���Ϊ��", 1000);
					}else{
						Bundle b = new Bundle();
						b.putString("content", mEt_content.getText().toString().trim());
						showFragment(b);
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
						imm.hideSoftInputFromWindow(mEt_content.getWindowToken(), 0); //ǿ�����ؼ���
					}
					return true;
				}
				return false;
			}
		});
		mBtn_keyboard = (Button) findViewById(R.id.btn_keyboard);
		mBtn_keyboard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(flag){
					mBtn_say.setVisibility(View.GONE);
					mEt_content.setVisibility(View.VISIBLE);
					flag = false;
				}else{
					mEt_content.setVisibility(View.GONE);
					mBtn_say.setVisibility(View.VISIBLE);
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
					imm.hideSoftInputFromWindow(mEt_content.getWindowToken(), 0); //ǿ�����ؼ���
					flag = true;
				}
			}
		});
		mBtn_say = (Button) findViewById(R.id.btn_say);
		switch (type) {
		case 0:
			mDetailTitlebar.setTitle("ϴ����ͼ");
			requestShop(SHLocationManager.getInstance().getLat(),SHLocationManager.getInstance().getLng());
			break;
		case 1:
			mDetailTitlebar.setTitle("һ�����");
			mLl_bottom_check.setVisibility(View.VISIBLE);
			requestShop(SHLocationManager.getInstance().getLat(),SHLocationManager.getInstance().getLng());
			break;
		case 2:
			mDetailTitlebar.setTitle("����Ԯ��");
			mLl_bottom_say.setVisibility(View.VISIBLE);
			requestShop(SHLocationManager.getInstance().getLat(),SHLocationManager.getInstance().getLng());
			break;
		case 3:
			mDetailTitlebar.setTitle("����ά��");
			mLl_bottom_say.setVisibility(View.VISIBLE);
			requestShop(SHLocationManager.getInstance().getLat(),SHLocationManager.getInstance().getLng());
			break;
		case 4:
			mDetailTitlebar.setTitle("����");
			mLl_bottom_say.setVisibility(View.VISIBLE);
			requestShop(SHLocationManager.getInstance().getLat(),SHLocationManager.getInstance().getLng());
			break;
		case 5:
			mDetailTitlebar.setTitle("ר�ҽ���");
			mLl_bottom_say.setVisibility(View.VISIBLE);
			mMapView.setVisibility(View.GONE);
			iv_zhuanjia.setVisibility(View.VISIBLE);
			break;
		}

		mTv_location = (TextView) findViewById(R.id.tv_location);
		mTv_location.setText("��ǰλ��:" + SHLocationManager.getInstance().getAddress());
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		mBaiduMap.setMapStatus(msu);
//		mBaiduMap.setMyLocationEnabled(true);
		initOverlay();
//		setListener();
	}

	private void requestShop(double lat,double lng) {
//		SHDialog.ShowProgressDiaolg(this, null);
		if(shanghuTask != null){
			shanghuTask.cancel(true);
		}
		shanghuTask = new SHPostTaskM();
		shanghuTask.setUrl(ConfigDefinition.URL + "shopquery.action");
		shanghuTask.setListener(this);
		shanghuTask.getTaskArgs().put("lat", lat);
		shanghuTask.getTaskArgs().put("lgt", lng);
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


	public void initOverlay() {
		// add marker overlay
		LatLng currentLatLng = new LatLng(SHLocationManager.getInstance().getLat(), SHLocationManager.getInstance().getLng());
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(currentLatLng);
		mBaiduMap.animateMapStatus(u);// ��λ����ǰλ��
		ooCurrent = new MarkerOptions().position(currentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding)).zIndex(5);
		mCenterMarker = (Marker) mBaiduMap.addOverlay(ooCurrent);// ��ǰλ��ͼ��
		addOverlay();
	}

	private void addOverlay() {
		if (jsonArray != null && jsonArray.length() > 0) {
			// InfoWindow mInfoWindow;
			OverlayOptions ooShop;
			View view = LayoutInflater.from(this).inflate(R.layout.popup_map, null);
			TextView tv_shop_name = (TextView) view.findViewById(R.id.tv_shop_name);
			TextView tv_shop_address = (TextView) view.findViewById(R.id.tv_shop_address);
			SHImageView iv_shop = (SHImageView) view.findViewById(R.id.iv_shop);
			RatingBar rating = (RatingBar) view.findViewById(R.id.rating);

			for (int i = 0; i < jsonArray.length(); i++) {
				LatLng point = null;
				try {
					point = new LatLng(jsonArray.getJSONObject(i).optJSONObject("baidulatitude").optDouble("lat"), jsonArray.getJSONObject(i).optJSONObject("baidulatitude").optDouble("lgt"));
					tv_shop_name.setText(jsonArray.getJSONObject(i).optString("shopname"));
					tv_shop_address.setText(jsonArray.getJSONObject(i).optString("shopaddress"));
					rating.setRating(Float.valueOf(jsonArray.getJSONObject(i).optString("shopscore").toString()));
					iv_shop.setNewImgForImageSrc(true);
//					 iv_shop.setBackgroundDrawable(getResources().getDrawable(R.drawable.dashboard_fault));
					iv_shop.setURL("http://www.baidu.com/img/bd_logo1.png");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ooShop = new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromView(view)).zIndex(i);
				mMarkerShop = (Marker) (mBaiduMap.addOverlay(ooShop));
			}
		}
		if(type != 2){
			setListener();
		}
	}

	private void setListener() {
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WashMapActivity.this, ShopDetailActivity.class);
				try {
					System.out.println(marker.getZIndex());
					intent.putExtra("shopid", jsonArray.getJSONObject(marker.getZIndex()).optString("shopid"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startActivity(intent);
				return true;
			}
		});

		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
			
			@Override
			public void onMapStatusChangeStart(MapStatus arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMapStatusChangeFinish(MapStatus arg0) {
				// TODO Auto-generated method stub
				mBaiduMap.clear();//��������еĸ���
				SHLocationManager.getInstance().reverseGeoCode(arg0.target);
				mCenterMarker.remove();
				ooCurrent = new MarkerOptions().position(arg0.target).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding)).zIndex(5);
				mCenterMarker = (Marker) mBaiduMap.addOverlay(ooCurrent);// ��ǰλ��ͼ��
				requestShop(arg0.target.latitude,arg0.target.longitude);
			}
			
			@Override
			public void onMapStatusChange(MapStatus arg0) {
				// TODO Auto-generated method stub
				
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
								deleteOldFile();
								SHToast.showToast(WashMapActivity.this, "¼��ʱ��̫��", SHToast.LENGTH_SHORT);
							} else {
								// ¼�����֮��...
								showFragment(null);
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
		
		SHLocationManager.getInstance().setReverseGeoListener(new OnGetGeoCoderResultListener() {
			
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
				// TODO Auto-generated method stub
				mTv_location.setText("��ǰλ��:" + arg0.getAddress());
			}
			
			@Override
			public void onGetGeoCodeResult(GeoCodeResult arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void showFragment(Bundle b){
		FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.anim_translate_up_from_bottom,R.anim.anim_translate_down_to_bottom);
		if(releaseFragment == null){
			releaseFragment = new ReleaseFragment();
			if(b != null){
				System.out.println("not null...");
				releaseFragment.setArguments(b);
			}
			fragmentTransaction.replace(R.id.frame_container, releaseFragment);
		}else{
			fragmentTransaction.remove(releaseFragment);
			releaseFragment = null;
		}
		fragmentTransaction.commit();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		SHLocationManager.getInstance().stop();
		mMapView.onDestroy();
		mMapView = null;
		SHLocationManager.getInstance().destroyGeo();
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
//			mIv_voice_Image = 
			mTvRecordDialogTxt = (TextView) mRecordDialog.findViewById(R.id.record_dialog_txt);
		}
		switch (flag) {
		case 1:
//			mIvRecVolume.setImageDrawable(getResources().getDrawable(R.drawable.img_talk_null));
			mIvRecVolume.setImageResource(R.drawable.img_talk_null);
			mTvRecordDialogTxt.setText("�ɿ���ָ��ȡ��¼��");
			break;

		default:
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
						Thread.sleep(200);
						recodeTime += 0.20;
						// ��ȡ����������dialog
						if (!moveState) {
							voiceValue = mAudioRecorder.getAmplitude();
							if(voiceValue < 600.0){
								volumeHandler.sendEmptyMessage(0);
							}else if(voiceValue < 1000.0){
								volumeHandler.sendEmptyMessage(1);
							}else if(voiceValue < 1200.0){
								volumeHandler.sendEmptyMessage(2);
							}else if(voiceValue < 1400.0){
								volumeHandler.sendEmptyMessage(3);
							}else if(voiceValue < 1600.0){
								volumeHandler.sendEmptyMessage(4);
							}else if(voiceValue < 1800.0){
								volumeHandler.sendEmptyMessage(5);
							}else if(voiceValue < 2000.0){
								volumeHandler.sendEmptyMessage(6);
							}else if(voiceValue < 3000.0){
								volumeHandler.sendEmptyMessage(7);
							}else if(voiceValue < 4000.0){
								volumeHandler.sendEmptyMessage(8);
							}else if(voiceValue < 6000.0){
								volumeHandler.sendEmptyMessage(9);
							}else if(voiceValue < 8000.0){
								volumeHandler.sendEmptyMessage(10);
							}else if(voiceValue < 10000.0){
								volumeHandler.sendEmptyMessage(11);
							}else if(voiceValue < 12000.0){
								volumeHandler.sendEmptyMessage(12);
							}else{
								volumeHandler.sendEmptyMessage(13);
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};
	private static int[] res_volumn = { R.drawable.record_animate_01, R.drawable.record_animate_02,
		R.drawable.record_animate_03,R.drawable.record_animate_04,R.drawable.record_animate_05,R.drawable.record_animate_06,R.drawable.record_animate_07,R.drawable.record_animate_08,R.drawable.record_animate_09,R.drawable.record_animate_10,R.drawable.record_animate_11,R.drawable.record_animate_12,R.drawable.record_animate_13,R.drawable.record_animate_14};
	    
	    static class ShowVolumeHandler extends Handler {
			@Override
			public void handleMessage(Message msg) {
				System.out.println(msg.what);
				mIvRecVolume.setImageResource(res_volumn[msg.what]);
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("username", SHEnvironment.getInstance().getLoginID());
		outState.putString("password", SHEnvironment.getInstance().getPassword());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		SHEnvironment.getInstance().setLoginId(savedInstanceState.getString("username"));
		SHEnvironment.getInstance().setPassword(savedInstanceState.getString("password"));
	}


}
