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
	private String tempcoor = "bd09ll";// ֮ǰ"gcj02"
	private double Lng, Lat;// ���ȣ�γ��
	private String address;
	private GeoCoder mSearch = null;// ���ڱ���ͷ�����
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
		mLocationClient.start();// ������λ
		
	}

	/**
	 * ʵ��ʵλ�ص�����
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
		option.setLocationMode(tempMode);// ���ö�λģʽ
		option.setCoorType(tempcoor);// ���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
		int span = 5000;
		option.setScanSpan(span);// ���÷���λ����ļ��ʱ��Ϊ5000ms
		option.setIsNeedAddress(true);// �������ַ
		mLocationClient.setLocOption(option);
	}

	/**
	 * ���ñ��뷴��������¼�
	 * 
	 * @param listener
	 */
	public void setReverseGeoListener(OnGetGeoCoderResultListener listener) {
		mSearch = GeoCoder.newInstance();
		// ��ʼ������ģ�飬ע���¼�����
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
