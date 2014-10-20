package com.sky.car.home;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
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
import com.sky.car.widget.SHImageView;
import com.sky.widget.SHDialog;

public class HomeFragment extends BaseFragment implements ITaskListener{

	private TextView mTv_onekey,mTv_check,mTv_manage,mTv_car_xilie,mTv_car_number,mTv_baoyang;
//	private SHPostTaskM carTask;
	private SHImageView iv_car_logo;
	public static JSONObject json;//当前车辆
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mDetailTitlebar.setTitle("车宝宝");
		mDetailTitlebar.setLeftButton(R.drawable.search, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		mDetailTitlebar.setRightButton(R.drawable.settings, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		mTv_onekey = (TextView) view.findViewById(R.id.tv_onekey);
		mTv_check = (TextView) view.findViewById(R.id.tv_check);
		mTv_manage = (TextView) view.findViewById(R.id.tv_manage);
		iv_car_logo = (SHImageView) view.findViewById(R.id.iv_car_logo);
		mTv_car_xilie = (TextView) view.findViewById(R.id.tv_car_xilie);
		mTv_car_number = (TextView) view.findViewById(R.id.tv_car_number);
		mTv_baoyang = (TextView) view.findViewById(R.id.tv_baoyang);
		OnClick onClick = new OnClick();
		mTv_onekey.setOnClickListener(onClick);
		mTv_check.setOnClickListener(onClick);
		mTv_manage.setOnClickListener(onClick);
		mTv_baoyang.setOnClickListener(onClick);
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
				startActivity(intent1);
				break;
			case R.id.tv_check:
				intent.putExtra("class", OnekeyWashFragment.class.getName());
				startActivity(intent);
				break;
			case R.id.tv_manage:
				intent.putExtra("class", MyCarsFragment.class.getName());
				startActivity(intent);
				break;
			case R.id.tv_baoyang:
				Intent intent_bao = new Intent(getActivity(),BaoYangActivity.class);
				startActivity(intent_bao);
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
}
