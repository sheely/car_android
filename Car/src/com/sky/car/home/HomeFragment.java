package com.sky.car.home;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.next.intf.ITaskListener;
import com.next.net.SHTask;
import com.sky.base.BaseFragment;
import com.sky.base.SHContainerActivity;
import com.sky.car.R;
import com.sky.car.adapter.TopAdvertPagerAdapter;
import com.sky.car.widget.SHImageView;
import com.sky.widget.SHDialog;

public class HomeFragment extends BaseFragment implements ITaskListener{

	private TextView mTv_onekey,mTv_check,mTv_manage,mTv_car_xilie,mTv_car_number,mTv_jinji,mTv_baoyang,mTv_baoxian,mTv_zhuanjia;
	private ViewPager mPagerView_TopAdvert;
	private SHImageView iv_car_logo;
	public static JSONObject json;//当前车辆
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				HomeFragment.this.setTopAdv();
				break;

			default:
				break;
			}
		}
	};
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mDetailTitlebar.setTitle("车宝宝");
		mDetailTitlebar.setLeftButton(null, null);
//		mDetailTitlebar.setLeftButton(R.drawable.search, new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		mDetailTitlebar.setRightButton(R.drawable.settings, new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		mTv_onekey = (TextView) view.findViewById(R.id.tv_onekey);
		mTv_check = (TextView) view.findViewById(R.id.tv_check);
		mTv_manage = (TextView) view.findViewById(R.id.tv_manage);
		iv_car_logo = (SHImageView) view.findViewById(R.id.iv_car_logo);
		mTv_car_xilie = (TextView) view.findViewById(R.id.tv_car_xilie);
		mTv_car_number = (TextView) view.findViewById(R.id.tv_car_number);
		mTv_jinji = (TextView) view.findViewById(R.id.tv_jinji);
		mTv_baoyang = (TextView) view.findViewById(R.id.tv_baoyang);
		mTv_baoxian = (TextView) view.findViewById(R.id.tv_baoxian);
		mTv_zhuanjia = (TextView) view.findViewById(R.id.tv_zhuanjia);
		OnClick onClick = new OnClick();
		mTv_onekey.setOnClickListener(onClick);
		mTv_check.setOnClickListener(onClick);
		mTv_manage.setOnClickListener(onClick);
		mTv_baoyang.setOnClickListener(onClick);
		mTv_jinji.setOnClickListener(onClick);
		mTv_baoxian.setOnClickListener(onClick);
		mTv_zhuanjia.setOnClickListener(onClick);
		try {
			json = new JSONObject(getActivity().getIntent().getStringExtra("car"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		SHDialog.ShowProgressDiaolg(getActivity(), null);
//		carTask = new SHPostTaskM();
//		carTask.setListener(HomeFragment.this);
//		carTask.setUrl(ConfigDefinition.URL+"mycarquery.action");
//		carTask.start();
//		System.out.println("add:"+SHApplication.getInstance().getAddress());
		mPagerView_TopAdvert = (ViewPager) view.findViewById(R.id.viewpager);
		setTopAdv();
	}

	public void setCurrentCar(){
		try {
			iv_car_logo.setNewImgForImageSrc(true);
			iv_car_logo.setURL(json.getString("carlogo"));
			mTv_car_xilie.setText(json.getString("carcategoryname")+json.getString("carseriesname"));
			mTv_car_number.setText(json.getString("carcardno"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setCurrentCar();
	}

	private class OnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getActivity(),SHContainerActivity.class);
			switch(v.getId()){
			case R.id.tv_onekey:
				Intent intent1 = new Intent(getActivity(),OneKeyWashActivity.class);
				intent.putExtra("type", 0);
				startActivity(intent1);
				break;
			case R.id.tv_check:
				Intent intent2 = new Intent(getActivity(),WashMapActivity.class);
				intent2.putExtra("type", 1);
				startActivity(intent2);
				break;
			case R.id.tv_manage:
				intent.putExtra("class", MyCarsFragment.class.getName());
				startActivity(intent);
				break;
			case R.id.tv_jinji:
				Intent intent_jin = new Intent(getActivity(),WashMapActivity.class);
				intent_jin.putExtra("type", 2);
				startActivity(intent_jin);
				break;
			case R.id.tv_baoyang:
				Intent intent_yang = new Intent(getActivity(),WashMapActivity.class);
				intent_yang.putExtra("type", 3);
				startActivity(intent_yang);
				break;
			case R.id.tv_baoxian:
				Intent intent_xian = new Intent(getActivity(),WashMapActivity.class);
				intent_xian.putExtra("type", 4);
				startActivity(intent_xian);
				break;
			case R.id.tv_zhuanjia:
				Intent intent_zhuan = new Intent(getActivity(),WashMapActivity.class);
				intent_zhuan.putExtra("type", 5);
				startActivity(intent_zhuan);
				break;
			}
			
		}
		
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
//		if(task == carTask){
//			System.out.println(task.getResult().toString());
//			JSONArray jsonArray = ((JSONObject) task.getResult()).getJSONArray("myCarList");
//			for(int i=0;i<jsonArray.length();i++){
//				JSONObject obj = jsonArray.getJSONObject(i);
//				if(obj.optInt("isactivited") == 1){
//					iv_car_logo.setNewImgForImageSrc(true);
//					iv_car_logo.setURL(obj.getString("carlogo"));
//					mTv_car_xilie.setText(obj.getString("carcategoryname")+obj.getString("carseriesname"));
//					mTv_car_number.setText(obj.getString("carcardno"));
//				}
//			}
//		}
	}

	@Override
	public void onTaskFailed(SHTask task) {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		SHDialog.showOneKeyDialog(getActivity(), task.getRespInfo().getMessage(), null);
	}

	@Override
	public void onTaskUpdateProgress(SHTask task, int count, int total) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskTry(SHTask task) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 图片广告
	 */
	private void setTopAdv() {

		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(R.drawable.banner1);
		list.add(R.drawable.banner2);
		list.add(R.drawable.banner3);
		TopAdvertPagerAdapter adapter = new TopAdvertPagerAdapter(getActivity(), list);
		mPagerView_TopAdvert.setAdapter(adapter);
//		int position = jsonArray.length() * 100;
		mHandler.postDelayed(mTopAdvertPageRunnable, 3000);
		mPagerView_TopAdvert.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				mHandler.removeCallbacks(mTopAdvertPageRunnable);
				mHandler.postDelayed(mTopAdvertPageRunnable, 3000);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private Runnable mTopAdvertPageRunnable = new Runnable() {
		public void run() {
			int position = mPagerView_TopAdvert.getCurrentItem();
			position = (position + 1) % 3;
			mPagerView_TopAdvert.setCurrentItem(position, true);
		}
	};

}
