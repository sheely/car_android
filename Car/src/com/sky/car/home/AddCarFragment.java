package com.sky.car.home;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.sky.base.BaseFragment;
import com.sky.base.SHContainerActivity;
import com.sky.car.R;
import com.sky.car.util.ConfigDefinition;
import com.sky.widget.SHDialog;
import com.sky.widget.SHDialog.DialogClickListener;
import com.sky.widget.SHToast;

public class AddCarFragment extends BaseFragment implements ITaskListener{

	private TextView tv_brand,tv_xilie,tv_pro,tv_area;
	private EditText et_no;
	private JSONObject jsonCar;
	private String brandID,xilieID,proID;
//	private LinearLayout ll_5;
	private Button btn_save,btn_delete;
	private SHPostTaskM saveTask,deleteTask;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		
		tv_brand = (TextView) view.findViewById(R.id.tv_brand);
		tv_xilie = (TextView) view.findViewById(R.id.tv_xilie);
		tv_pro = (TextView) view.findViewById(R.id.tv_pro);
		tv_area = (TextView) view.findViewById(R.id.tv_area);
		et_no = (EditText) view.findViewById(R.id.et_no);
		btn_save = (Button) view.findViewById(R.id.btn_save);
		btn_delete = (Button) view.findViewById(R.id.btn_delete);
//		ll_5 = (LinearLayout) view.findViewById(R.id.ll_5);
		OnClick onClick = new OnClick();
		tv_brand.setOnClickListener(onClick);
		tv_xilie.setOnClickListener(onClick);
		tv_pro.setOnClickListener(onClick);
		tv_area.setOnClickListener(onClick);
		et_no.setOnClickListener(onClick);
		btn_save.setOnClickListener(onClick);
		btn_delete.setOnClickListener(onClick);
		
		if("add".equals(getActivity().getIntent().getStringExtra("option"))){
			mDetailTitlebar.setTitle("添加车辆");
		}else{
			mDetailTitlebar.setTitle("车辆信息");
			try {
				jsonCar = new JSONObject(getActivity().getIntent().getStringExtra("car"));
				System.out.println("jsoncar:"+jsonCar);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setInfo();
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_add_car, container, false);
		return view;
	}

	private void setInfo(){
		try {
			brandID = jsonCar.getString("carcategoryid");
			xilieID = jsonCar.getString("carseriesid");
			proID = jsonCar.getString("provinceid");
			et_no.setText(jsonCar.getString("carcardno"));
			tv_brand.setText(jsonCar.getString("carcategoryname"));
			tv_xilie.setText(jsonCar.getString("carseriesname"));
			tv_pro.setText(jsonCar.getString("provincename"));
			tv_area.setText(jsonCar.getString("alphabetname"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class OnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.tv_brand:
				Intent intent = new Intent(getActivity(),SHContainerActivity.class);
				intent.putExtra("class", SelectBrandFragment.class.getName());
				startActivityForResult(intent, 0);
				break;
			case R.id.tv_xilie:
				if(brandID == null){
					SHToast.showToast(getActivity(), "请先选择品牌", 1000);
					return;
				}
				Intent intent2 = new Intent(getActivity(),SHContainerActivity.class);
				intent2.putExtra("class", SelectXilieFragment.class.getName());
				intent2.putExtra("carcategoryid", brandID);
				startActivityForResult(intent2, 1);
				break;
			case R.id.tv_pro:
				Intent intent3 = new Intent(getActivity(),SHContainerActivity.class);
				intent3.putExtra("class", SelectProFragment.class.getName());
				startActivityForResult(intent3, 2);
				break;
			case R.id.tv_area:
				Intent intent4 = new Intent(getActivity(),SHContainerActivity.class);
				intent4.putExtra("class", SelectAreaFragment.class.getName());
				startActivityForResult(intent4, 3);
				break;
			case R.id.et_no:
//				ll_5.setVisibility(View.VISIBLE);
				break;
			case R.id.btn_save:
//				if("add".equals(getActivity().getIntent().getStringExtra("option"))){
					if(brandID == null){
						SHToast.showToast(getActivity(), "请选择品牌", 1000);
						return;
					}
					if(xilieID == null){
						SHToast.showToast(getActivity(), "请选择车系", 1000);
						return;
					}
					if(proID == null || et_no.getText().toString().trim().length() <= 0){
						SHToast.showToast(getActivity(), "车牌号不正确", 1000);
						return;
					}
					requestSave();
//				}
				break;
			case R.id.btn_delete:
				SHDialog.showDoubleKeyDialog(getActivity(), "确定删除该车辆？", new DialogClickListener() {
					
					@Override
					public void confirm() {
						// TODO Auto-generated method stub
						SHDialog.ShowProgressDiaolg(getActivity(), null);
						deleteTask = new SHPostTaskM();
						deleteTask.setListener(AddCarFragment.this);
						deleteTask.setUrl(ConfigDefinition.URL+"mycarmaintanance.action");
						try {
							deleteTask.getTaskArgs().put("carid", jsonCar.getString("carid"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						deleteTask.getTaskArgs().put("optype", 2);
						deleteTask.start();
					}
					
					@Override
					public void cancel() {
						// TODO Auto-generated method stub
						
					}
				});
				break;
			}
		}
		
	}

	private void requestSave(){
		SHDialog.ShowProgressDiaolg(getActivity(), null);
		saveTask = new SHPostTaskM();
		saveTask.setListener(this);
		saveTask.setUrl(ConfigDefinition.URL+"mycarmaintanance.action");
		saveTask.getTaskArgs().put("carseriesid", xilieID);
		try {
			if("detail".equals(getActivity().getIntent().getStringExtra("option"))){
				saveTask.getTaskArgs().put("carid", jsonCar.getString("carid"));
				saveTask.getTaskArgs().put("optype", 1);
			}else{
				saveTask.getTaskArgs().put("optype", 0);
				saveTask.getTaskArgs().put("carid", "");
			}
			saveTask.getTaskArgs().put("carcategoryid", brandID);
			saveTask.getTaskArgs().put("provinceid", proID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		saveTask.getTaskArgs().put("alphabetname", tv_area.getText().toString().trim());
		saveTask.getTaskArgs().put("carcardno", et_no.getText().toString().trim());
		saveTask.start();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0 && data != null && data.getStringExtra("brand") != null){
			try {
				JSONObject jsonBrand = new JSONObject(data.getStringExtra("brand"));
				tv_brand.setText(jsonBrand.getString("carcategoryname"));
				brandID = jsonBrand.getString("carcategoryid");
//				System.out.println(jsonBrand.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(requestCode == 1 && data != null && data.getStringExtra("xilie") != null){
			try {
				JSONObject jsonXilie = new JSONObject(data.getStringExtra("xilie"));
				tv_xilie.setText(jsonXilie.getString("carseriesidname"));
				xilieID = jsonXilie.getString("carseriesidid");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(requestCode == 2 && data != null && data.getStringExtra("pro") != null){
			try {
				JSONObject jsonPro = new JSONObject(data.getStringExtra("pro"));
				tv_pro.setText(jsonPro.getString("provincename"));
				proID = jsonPro.getString("provinceid");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(requestCode == 3 && data != null && data.getStringExtra("area") != null){
			tv_area.setText(data.getStringExtra("area"));
		}
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		if(task == deleteTask){
			SHToast.showToast(getActivity(), "刪除成功！", 1000);
			getActivity().finish();
		}else{
			SHToast.showToast(getActivity(), "保存成功！", 1000);
			System.out.println(task.getResult().toString());
			getActivity().finish();
		}
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
