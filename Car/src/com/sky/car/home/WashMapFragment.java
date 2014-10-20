package com.sky.car.home;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

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
import com.sky.base.BaseFragment;
import com.sky.base.SHApplication;
import com.sky.base.SHContainerActivity;
import com.sky.car.R;
import com.sky.car.widget.SHImageView;

/**
 * 洗车地图
 * 
 * @author skypan
 * 
 */
public class WashMapFragment extends BaseFragment {

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private Marker mMarkerShop;// 当前位置标记
	JSONArray jsonArray = null;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mDetailTitlebar.setTitle("洗车地图");
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		mBaiduMap.setMapStatus(msu);
		
		try {
			jsonArray = new JSONArray(getActivity().getIntent().getStringExtra("jsonArray"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		initOverlay();
		setListener();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_wash_map, container, false);
		return view;
	}

	public void initOverlay() {
		// add marker overlay
		LatLng currentLatLng = new LatLng(SHApplication.getInstance().getLat(), SHApplication.getInstance().getLng());
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(currentLatLng);
		mBaiduMap.animateMapStatus(u);// 定位到当前位置
		OverlayOptions ooCurrent = new MarkerOptions().position(currentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding)).zIndex(5);
		mBaiduMap.addOverlay(ooCurrent);// 当前位置图标
		if (jsonArray != null && jsonArray.length() > 0) {
			// InfoWindow mInfoWindow;
			OverlayOptions ooShop;
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_map, null);
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
//					iv_shop.setNewImgForImageSrc(true);
//					iv_shop.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.dashboard_fault));
					iv_shop.setURL("http://www.baidu.com/img/bd_logo1.png");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ooShop = new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromView(view)).zIndex(i);
				mMarkerShop = (Marker) (mBaiduMap.addOverlay(ooShop));
			}
		}
	}

	private void setListener(){
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),SHContainerActivity.class);
				intent.putExtra("class", ShopDetailFragment.class.getName());
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

}
