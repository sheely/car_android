package com.sky.car.home;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.sky.base.BaseFragment;
import com.sky.base.SHApplication;
import com.sky.car.R;

public class MapFragment extends BaseFragment {

	private TextView mTv_location,mTv_distance;
	private Button mBtn_daohang;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	JSONObject json = null;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mDetailTitlebar.setTitle("地图");
		mTv_location = (TextView) view.findViewById(R.id.tv_location);
		mTv_distance = (TextView) view.findViewById(R.id.tv_distance);
		mBtn_daohang = (Button) view.findViewById(R.id.btn_daohang);
		mTv_location.setText("当前位置："+SHApplication.getInstance().getAddress());
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
		mBaiduMap.setMapStatus(msu);
		initOverlay();
		SHApplication.getInstance().setNewLocationListener(new BDLocationListener() {
			
			@Override
			public void onReceiveLocation(BDLocation arg0) {
				// TODO Auto-generated method stub
				if(arg0 == null){
					mTv_location.setText("当前位置：正在定位...");
					mTv_distance.setText("正在定位...");
				}else{
					mTv_location.setText("当前位置："+SHApplication.getInstance().getAddress());
					LatLng currentLatLng = null;
					try {
						currentLatLng = new LatLng(json.getJSONObject("baidulatitude").optDouble("lat"), json.getJSONObject("baidulatitude").optDouble("lgt"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					int d = (int) DistanceUtil.getDistance(currentLatLng, new LatLng(SHApplication.getInstance().getLat(), SHApplication.getInstance().getLng()));
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
					mUri = Uri.parse("geo:"+json.getJSONObject("baidulatitude").optDouble("lat")+","+json.getJSONObject("baidulatitude").optDouble("lgt")+"?q="+json.opt("shopname")+":"+json.optString("shopaddress"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent mIntent = new Intent(Intent.ACTION_VIEW,mUri);
				startActivity(mIntent);	
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_map, container, false);
		return view;
	}

	public void initOverlay() {
		// add marker overlay
		LatLng currentLatLng = null;
		try {
			json = new JSONObject(getActivity().getIntent().getStringExtra("json"));
			currentLatLng = new LatLng(json.getJSONObject("baidulatitude").optDouble("lat"), json.getJSONObject("baidulatitude").optDouble("lgt"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * 计算距离
		 */
		int d = (int) DistanceUtil.getDistance(currentLatLng, new LatLng(SHApplication.getInstance().getLat(), SHApplication.getInstance().getLng()));
		if(d == -1){
			mTv_distance.setText("距离未知");
		}else if(d >= 1000){
			mTv_distance.setText("距离当前位置"+new DecimalFormat("0.0").format((double)d/1000)+"km");
		}else{
			mTv_distance.setText("距离当前位置"+d+"m");
		}
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(currentLatLng);
		mBaiduMap.animateMapStatus(u);// 定位到当前位置
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_map, null);
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
	public void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("destory.....");
		mMapView.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		System.out.println("destory view..");
		super.onDestroyView();
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
		Activity activity = getActivity();
		System.out.println(activity);
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			getActivity().finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		 try {
	            Field field = Fragment.class.getDeclaredField("mChildFragmentManager");
	            field.setAccessible(true);
	            field.set(this, null);
	        }catch (Exception e){
	        	e.printStackTrace();
	        }
	}
	
}
