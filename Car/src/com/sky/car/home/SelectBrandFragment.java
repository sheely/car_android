package com.sky.car.home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

public class SelectBrandFragment extends BaseFragment implements ITaskListener{

	private SHPostTaskM brandTask;
	private CarsAdapter adapter;
	private ListView lv_brand;
	JSONArray jsonArray;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mDetailTitlebar.setTitle("—°‘Ò∆∑≈∆");
		lv_brand = (ListView) view.findViewById(R.id.lv_brand);
		lv_brand.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),SHContainerActivity.class);
				intent.putExtra("class", AddCarFragment.class.getName());
				try {
					intent.putExtra("brand", jsonArray.getJSONObject(position).toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getActivity().setResult(0, intent);
				getActivity().finish();
			}
		});
		SHDialog.ShowProgressDiaolg(getActivity(), null);
		brandTask = new SHPostTaskM();
		brandTask.setListener(this);
		brandTask.setUrl(ConfigDefinition.URL+"carcategoryquery.action");
		brandTask.start();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_select_brand, container, false);
		return view;
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		System.out.println(task.getResult().toString());
		jsonArray = ((JSONObject) task.getResult()).getJSONArray("carcategorys");
		adapter = new CarsAdapter(getActivity(),1);
		adapter.setData(jsonArray);
		lv_brand.setAdapter(adapter);
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
