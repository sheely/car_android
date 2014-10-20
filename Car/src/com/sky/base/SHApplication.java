package com.sky.base;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.net.Uri;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MyLocationData;
import com.next.app.StandardApplication;

public class SHApplication extends StandardApplication {

	private ArrayList<BaseActivity> activity_list = new ArrayList<BaseActivity>();
	/**
	 * location
	 */
	public LocationClient mLocationClient;
	public GeofenceClient mGeofenceClient;
	public MyLocationListener mMyLocationListener;
	private BDLocationListener newLocationListener;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	// private MyLocationData locData;
	// public MyLocationData getLocData() {
	// return locData;
	// }

	private String tempcoor = "bd09ll";// 之前"gcj02"
	private double Lng, Lat;// 经度，纬度
	private String address;

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
		this.newLocationListener = newLocationListener;
		mLocationClient.registerLocationListener(newLocationListener);
	}

	/**
	 * 
	 * @param _uri
	 */
	public void open(String _uri) {
		Uri uri = Uri.parse(_uri);
		this.open(uri);
	}

	public void open(Activity activity, String _uri) {
		Uri uri = Uri.parse(_uri);
		this.open(uri);
	}

	public void onCreate() {
		super.onCreate();
		mLocationClient = new LocationClient(this.getApplicationContext());
		// LocationClientOption option = new LocationClientOption();
		// option.setOpenGps(true);// 打开gps
		// option.setCoorType("bd09ll"); // 设置坐标类型
		// option.setScanSpan(10000);
		// mLocationClient.setLocOption(option);
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		mGeofenceClient = new GeofenceClient(getApplicationContext());
		InitLocation();
		mLocationClient.start();// 启动定位
		SDKInitializer.initialize(this);
	}

	public void addActivity(BaseActivity a) {
		activity_list.add(a);
	}

	/**
	 * 
	 */
	public void exitApplication() {
		for (BaseActivity a : activity_list) {
			a.finish();
		}
	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			// if(location != null){
			setLng(location.getLongitude());
			setLat(location.getLatitude());
			setAddress(location.getAddrStr());

			// System.out.println(location.getAddrStr());
			// System.out.println(getLng()+","+getLat());

			// locData = new MyLocationData.Builder()
			// .accuracy(location.getRadius())
			// // 此处设置开发者获取到的方向信息，顺时针0-360
			// .direction(100).latitude(location.getLatitude())
			// .longitude(location.getLongitude()).build();
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
	 * 
	 * @param uri
	 */
	public void open(Uri uri) {
		// String scheme = uri.getScheme();
		String host = uri.getHost();
		// String query = uri.getQuery();
		SHIntent intent = new SHIntent(SHApplication.getInstance(), SHContainerActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		String classname = SHModuleManager.getInstance().getModuleName(host);
		intent.putExtra("class", classname);
		SHApplication.getInstance().startActivity(intent);
	}

	/**
	 * 
	 * @param args
	 */
	public <K> void open(SHArgsContent<K, Object> args) {
		SHIntent intent = new SHIntent((Context) args.getDelegate(), SHContainerActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("class", args.getTarget());
		startActivity(intent);
	}

	/**
	 * 
	 * @return
	 */
	public static SHApplication getInstance() {

		return (SHApplication) StandardApplication.getInstance();
	}
}
