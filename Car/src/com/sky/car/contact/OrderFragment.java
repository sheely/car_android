package com.sky.car.contact;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.sky.base.BaseFragment;
import com.sky.car.R;
import com.sky.car.adapter.OrderAdapter;
import com.sky.car.util.ConfigDefinition;
import com.sky.car.widget.SHExpandableListView;
import com.sky.widget.SHDialog;

public class OrderFragment extends BaseFragment implements ITaskListener{

	private SHPostTaskM orderTask;
	private OrderAdapter adapter;
	private JSONArray jsonArray;
	private SHExpandableListView mLv_expand;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mLv_expand = (SHExpandableListView) view.findViewById(R.id.lv_expand);
		requestOrder();
		adapter = new OrderAdapter(getActivity());
		mLv_expand.setAdapter(adapter);
		mLv_expand.setCanRefresh(true);
		mLv_expand.setCanLoadMore(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_order, container, false);
		return view;
	}

	private void requestOrder(){
		SHDialog.ShowProgressDiaolg(getActivity(), null);
		orderTask = new SHPostTaskM();
		orderTask.setListener(this);
		orderTask.setUrl(ConfigDefinition.URL+"ordersquery.action");
		orderTask.getTaskArgs().put("ordertype", 99);
		orderTask.getTaskArgs().put("pageno", 1);
		orderTask.getTaskArgs().put("pagesize", 10);
		orderTask.start();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		System.out.println(task.getResult().toString());
		JSONObject json = (JSONObject) task.getResult();
		jsonArray = json.optJSONArray("orders");
		adapter.setData(jsonArray);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onTaskFailed(SHTask task) {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		SHDialog.showDoubleKeyDialog(getActivity(), task.getRespInfo().getMessage(), null);
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
