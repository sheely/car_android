package com.sky.car.util;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.sky.base.SHApplication;

public class SHLocationManager {
	public LocationClient mLocationClient;
	public GeofenceClient mGeofenceClient;
	public MyLocationListener mMyLocationListener;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor = "bd09ll";// 之前"gcj02"
	private double Lng, Lat;// 经度，纬度
	private String address;
	private GeoCoder mSearch = null;// 用于编码和反编码
	private static SHLocationManager locationManager;

	public static SHLocationManager getInstance() {
		if (locationManager == null) {
			locationManager = new SHLocationManager();
		}
		return locationManager;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLng() {
		return Lng;
	}

	public void setLng(double lng) {
		Lng = lng;
	}

	public double getLat() {
		return Lat;
	}

	public void setLat(double lat) {
		Lat = lat;
	}

	public void setNewLocationListener(BDLocationListener newLocationListener) {
		mLocationClient.registerLocationListener(newLocationListener);
	}

	public void start() {
		if(mLocationClient == null){
			mLocationClient = new LocationClient(SHApplication.getInstance().getApplicationContext());
		}
		if(mMyLocationListener == null){
			mMyLocationListener = new MyLocationListener();
		}
		mLocationClient.registerLocationListener(mMyLocationListener);
		if(mGeofenceClient == null){
			mGeofenceClient = new GeofenceClient(SHApplication.getInstance().getApplicationContext());
		}
		InitLocation();
		mLocationClient.start();// 启动定位
		
	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			setLng(location.getLongitude());
			setLat(location.getLatitude());
			setAddress(location.getAddrStr());
			stop();
		}
	}

	private void InitLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);// 设置定位模式
		option.setCoorType(tempcoor);// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 5000;
		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);// 反编码地址
		mLocationClient.setLocOption(option);
	}

	/**
	 * 设置编码反编码监听事件
	 * 
	 * @param listener
	 */
	public void setReverseGeoListener(OnGetGeoCoderResultListener listener) {
		mSearch = GeoCoder.newInstance();
		// 初始化搜索模块，注册事件监听
		mSearch.setOnGetGeoCodeResultListener(listener);
	}

	public void reverseGeoCode(LatLng latlng) {
		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latlng));
	}

	public void stop() {
		if(mLocationClient != null){
			mLocationClient.stop();
		}
	}
	
	public void destroyGeo(){
		if(mSearch != null){
			mSearch.destroy();
		}
	}
}
