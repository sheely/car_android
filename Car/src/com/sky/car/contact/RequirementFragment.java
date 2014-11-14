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
import com.sky.car.adapter.RequireAdapter;
import com.sky.car.util.ConfigDefinition;
import com.sky.car.widget.SHListView;
import com.sky.car.widget.SHListView.OnLoadMoreListener;
import com.sky.car.widget.SHListView.OnRefreshListener;
import com.sky.widget.SHDialog;

public class RequirementFragment extends BaseFragment implements ITaskListener{

	private SHListView mLv_require;
	private SHPostTaskM requireTask;
	private RequireAdapter adapter;
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mLv_require = (SHListView) view.findViewById(R.id.lv_require);
		mLv_require.setCanRefresh(true);
		mLv_require.setCanLoadMore(true);  
		mLv_require.setAutoLoadMore(true);
		mLv_require.setMoveToFirstItemAfterRefresh(true);
		mLv_require.setDoRefreshOnUIChanged(false);// 进入界面就refresh  false
		adapter = new RequireAdapter(getActivity());
		mLv_require.setAdapter(adapter);
		mLv_require.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				System.out.println("refresh....");
				requireTask();
//				mLv_require.setDoRefreshOnUIChanged(false);
			}
		});
		mLv_require.setOnLoadListener(new OnLoadMoreListener() {
			
			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				
			}
		});
		requireTask();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_require, container, false);
		return view;
	}

	private void requireTask(){
		requireTask = new SHPostTaskM();
		requireTask.setUrl(ConfigDefinition.URL+"acceptquestionlist.action");
		requireTask.setListener(this);
		requireTask.getTaskArgs().put("pageno", 1);
		requireTask.getTaskArgs().put("pagesize", 20);
		requireTask.start();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		mLv_require.onRefreshComplete();
		mLv_require.onLoadMoreComplete();
		JSONObject json = (JSONObject) task.getResult();
		JSONArray jsonArray = json.optJSONArray("questions");
		adapter.setJsonArray(jsonArray);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onTaskFailed(SHTask task) {
		// TODO Auto-generated method stub
		SHDialog.showOneKeyDialog(getActivity(), task.getRespInfo().getMessage(), null);
		mLv_require.onRefreshComplete();
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
