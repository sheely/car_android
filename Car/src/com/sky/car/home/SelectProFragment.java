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
import android.widget.GridView;

import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.sky.base.BaseFragment;
import com.sky.base.SHContainerActivity;
import com.sky.car.R;
import com.sky.car.adapter.ProAdapter;
import com.sky.car.util.ConfigDefinition;
import com.sky.widget.SHDialog;

public class SelectProFragment extends BaseFragment implements ITaskListener{

	private SHPostTaskM proTask;
	private GridView gv_pro;
	ProAdapter adapter;
	private JSONArray jsonArray;
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mDetailTitlebar.setTitle("Ê¡·ÝÑ¡Ôñ");
		gv_pro = (GridView) view.findViewById(R.id.gv_pro);
		gv_pro.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),SHContainerActivity.class);
				intent.putExtra("class", AddCarFragment.class.getName());
				try {
					intent.putExtra("pro", jsonArray.getJSONObject(position).toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getActivity().setResult(0, intent);
				getActivity().finish();
			}
		});
		requestPro();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_select_pro, container, false);
		return view;
	}

	private void requestPro(){
		SHDialog.ShowProgressDiaolg(getActivity(), null);
		proTask = new SHPostTaskM();
		proTask.setListener(this);
		proTask.setUrl(ConfigDefinition.URL+"provicequery.action");
		proTask.start();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		System.out.println(task.getResult().toString());
		jsonArray = ((JSONObject) task.getResult()).getJSONArray("provinces");
		adapter = new ProAdapter(getActivity());
		adapter.setData(jsonArray);
		gv_pro.setAdapter(adapter);
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
