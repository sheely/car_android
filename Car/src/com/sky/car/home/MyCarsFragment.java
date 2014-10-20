package com.sky.car.home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.sky.base.BaseFragment;
import com.sky.base.SHContainerActivity;
import com.sky.car.R;
import com.sky.car.adapter.CarsAdapter;
import com.sky.car.util.ConfigDefinition;
import com.sky.widget.SHDialog;

public class MyCarsFragment extends BaseFragment implements ITaskListener{

	private ListView lv_cars;
	private CarsAdapter adapter;
	private Button btn_add;
	JSONArray jsonArray;
	SHPostTaskM getCarsTask;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mDetailTitlebar.setTitle("ÎÒµÄ³µÁ¾");
		lv_cars = (ListView) view.findViewById(R.id.lv_cars);
		btn_add = (Button) view.findViewById(R.id.btn_add);
		adapter = new CarsAdapter(getActivity(),0);
		adapter.setFragment(MyCarsFragment.this);
		lv_cars.setAdapter(adapter);
		lv_cars.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),SHContainerActivity.class);
				intent.putExtra("class", AddCarFragment.class.getName());
				try {
					intent.putExtra("car", jsonArray.getJSONObject(position).toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				intent.putExtra("option", "detail");
				startActivity(intent);
			}
		});
		btn_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),SHContainerActivity.class);
				intent.putExtra("class", AddCarFragment.class.getName());
				intent.putExtra("option", "add");
				startActivity(intent);
			}
		});
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		requestMyCars();
	}

	public void requestMyCars(){
		SHDialog.ShowProgressDiaolg(getActivity(), null);
		getCarsTask = new SHPostTaskM();
		getCarsTask.setListener(MyCarsFragment.this);
		getCarsTask.setUrl(ConfigDefinition.URL+"mycarquery.action");
		getCarsTask.start();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_my_cars, container, false);
		return view;
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		if(task == getCarsTask){
			System.out.println(task.getResult().toString());
			jsonArray = ((JSONObject) task.getResult()).getJSONArray("mycars");
			adapter.setData(jsonArray);
			adapter.notifyDataSetChanged();
		}else{
			requestMyCars();
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
