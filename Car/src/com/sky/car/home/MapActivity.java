package com.sky.car.home;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.next.util.SHEnvironment;
import com.sky.base.BaseNormalActivity;
import com.sky.base.SHApplication;
import com.sky.car.R;
import com.sky.car.util.SHLocationManager;
import com.sky.car.util.UserInfoManager;

public class MapActivity extends BaseNormalActivity {

	private TextView mTv_location,mTv_distance;
	private Button mBtn_daohang;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	JSONObject json = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.fragment_map);
		super.onCreate(savedInstanceState);
		mDetailTitlebar.setTitle("地图");
		mTv_location = (TextView) findViewById(R.id.tv_location);
		mTv_distance = (TextView) findViewById(R.id.tv_distance);
		mBtn_daohang = (Button) findViewById(R.id.btn_daohang);
		mTv_location.setText("当前位置："+SHLocationManager.getInstance().getAddress());
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
		mBaiduMap.setMapStatus(msu);
		initOverlay();
		SHLocationManager.getInstance().setNewLocationListener(new BDLocationListener() {
			
			@Override
			public void onReceiveLocation(BDLocation arg0) {
				// TODO Auto-generated method stub
				if(arg0 == null){
					mTv_location.setText("当前位置：正在定位...");
					mTv_distance.setText("正在定位...");
				}else{
					mTv_location.setText("当前位置："+SHLocationManager.getInstance().getAddress());
					LatLng currentLatLng = null;
					try {
						currentLatLng = new LatLng(json.getJSONObject("baidulatitude").optDouble("lat"), json.getJSONObject("baidulatitude").optDouble("lgt"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					int d = (int) DistanceUtil.getDistance(currentLatLng, new LatLng(SHLocationManager.getInstance().getLat(), SHLocationManager.getInstance().getLng()));
					if(d == -1){
						mTv_distance.setText("距离未知");
					}else if(d >= 1000){
						mTv_distance.setText("距离当前位置"+new DecimalFormat("0.0").format((double)d/1000)+"km");
					}else{
						mTv_distance.setText("距离当前位置"+d+"m");
					}
				}
			}
		});
		mBtn_daohang.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri mUri = null;
				try {
					mUri = Uri.parse("geo:"+json.getJSONObject("baidulatitude").optDouble("lat")+","+json.getJSONObject("baidulatitude").optDouble("lgt"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent mIntent = new Intent(Intent.ACTION_VIEW,mUri);
				startActivity(mIntent);	
			}
		});
	}

	public void initOverlay() {
		// add marker overlay
		LatLng currentLatLng = null;
		try {
			json = new JSONObject(getIntent().getStringExtra("json"));
			currentLatLng = new LatLng(json.getJSONObject("baidulatitude").optDouble("lat"), json.getJSONObject("baidulatitude").optDouble("lgt"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * 计算距离
		 */
		int d = (int) DistanceUtil.getDistance(currentLatLng, new LatLng(SHLocationManager.getInstance().getLat(), SHLocationManager.getInstance().getLng()));
		if(d == -1){
			mTv_distance.setText("距离未知");
		}else if(d >= 1000){
			mTv_distance.setText("距离当前位置"+new DecimalFormat("0.0").format((double)d/1000)+"km");
		}else{
			mTv_distance.setText("距离当前位置"+d+"m");
		}
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(currentLatLng);
		mBaiduMap.animateMapStatus(u);// 定位到当前位置
		View view = LayoutInflater.from(this).inflate(R.layout.popup_map, null);
		TextView tv_shop_name = (TextView) view.findViewById(R.id.tv_shop_name);
		TextView tv_shop_address = (TextView) view.findViewById(R.id.tv_shop_address);
		RatingBar rating = (RatingBar) view.findViewById(R.id.rating);
		tv_shop_name.setText(json.optString("shopname"));
		tv_shop_address.setText(json.optString("shopaddress"));
		System.out.println(json.optString("shopscore"));
		rating.setRating(Float.valueOf(json.optString("shopscore")));
		OverlayOptions ooCurrent = new MarkerOptions().position(currentLatLng).icon(BitmapDescriptorFactory.fromView(view)).zIndex(5);
		mBaiduMap.addOverlay(ooCurrent);// 当前位置图标
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mMapView.onResume();
//		System.out.println(UserInfoManager.getInstance().getUserId());
//		System.out.println(UserInfoManager.getInstance().getName());
		SHEnvironment.getInstance().setLoginId(UserInfoManager.getInstance().getName());
		SHEnvironment.getInstance().setPassword(UserInfoManager.getInstance().getPassword());
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mMapView.onDestroy();
		super.onDestroy();
	}
	
}
