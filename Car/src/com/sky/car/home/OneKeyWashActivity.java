package com.sky.car.home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.sky.base.BaseNormalActivity;
import com.sky.base.SHApplication;
import com.sky.base.SHContainerActivity;
import com.sky.car.R;
import com.sky.car.adapter.ShopAdapter;
import com.sky.car.util.ConfigDefinition;
import com.sky.car.widget.SHListView;
import com.sky.car.widget.SHListView.OnRefreshListener;
import com.sky.widget.SHDialog;

public class OneKeyWashActivity extends BaseNormalActivity implements ITaskListener {

	private SHListView mLv_shop;
	private SHPostTaskM shanghuTask;
	private TextView tv_tip;
	private EditText mEt_keyword;
	ShopAdapter adapter;
	JSONArray jsonArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.fragment_onekey_wash);
		super.onCreate(savedInstanceState);
		mDetailTitlebar.setTitle("商户");
		mDetailTitlebar.setRightButton(R.drawable.icon_map, new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(OneKeyWashActivity.this, WashMapActivity.class);
				intent.putExtra("jsonArray", jsonArray.toString());
				intent.putExtra("type", 0);//0:一键洗车
				startActivity(intent);
			}
		});
		mLv_shop = (SHListView) findViewById(R.id.lv_shop);
		tv_tip = (TextView) findViewById(R.id.tv_tip);
		mEt_keyword = (EditText) findViewById(R.id.et_keyword);
		mEt_keyword.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mEt_keyword.getWindowToken(), 0); // 强制隐藏键盘
				SHDialog.ShowProgressDiaolg(OneKeyWashActivity.this, null);
				requestShangHu();
				return true;
			}
		});
		// mEt_keyword.setOnKeyListener(new OnKeyListener() {
		//
		// @Override
		// public boolean onKey(View v, int keyCode, KeyEvent event) {
		// // TODO Auto-generated method stub
		// if(keyCode==KeyEvent.KEYCODE_ENTER){
		// System.out.println("fu:"+mEt_keyword.getText().toString().trim());
		// SHDialog.ShowProgressDiaolg(getActivity(), null);
		// requestShangHu();
		// }
		// return false;
		// }
		// });
		mLv_shop.setCanLoadMore(true);
		mLv_shop.setCanRefresh(true);
		mLv_shop.setAutoLoadMore(true);
		mLv_shop.setMoveToFirstItemAfterRefresh(true);
		mLv_shop.setDoRefreshOnUIChanged(true);// 进入界面就refresh
		adapter = new ShopAdapter(this);
		mLv_shop.setAdapter(adapter);
		mLv_shop.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				requestShangHu();
				mLv_shop.setDoRefreshOnUIChanged(false);
			}
		});
		// requestShangHu();
		mLv_shop.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				// System.out.println("position:"+position);
				Intent intent = new Intent(OneKeyWashActivity.this, ShopDetailActivity.class);
				try {
					intent.putExtra("shopid", jsonArray.getJSONObject(position - 1).getString("shopid"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startActivity(intent);
			}
		});
	}

	private void requestShangHu() {
		// SHDialog.ShowProgressDiaolg(getActivity(), null);
		shanghuTask = new SHPostTaskM();
		shanghuTask.setUrl(ConfigDefinition.URL + "shopquery.action");
		shanghuTask.setListener(this);
		shanghuTask.getTaskArgs().put("lat", SHApplication.getInstance().getLat());
		shanghuTask.getTaskArgs().put("lgt", SHApplication.getInstance().getLng());
		shanghuTask.getTaskArgs().put("keyname", mEt_keyword.getText().toString().trim());
		shanghuTask.getTaskArgs().put("maptype", 0);
		shanghuTask.getTaskArgs().put("opertype", 0);
		shanghuTask.getTaskArgs().put("pageno", 1);
		shanghuTask.getTaskArgs().put("pagesize", 20);
		shanghuTask.getTaskArgs().put("ishaswash", 1);
		shanghuTask.getTaskArgs().put("ishascheck", 0);
		shanghuTask.getTaskArgs().put("ishasmaintainance", 0);
		shanghuTask.getTaskArgs().put("ishasurgentrescure", 0);
		shanghuTask.getTaskArgs().put("ishassellinsurance", 0);
		shanghuTask.start();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		System.out.println(task.getResult().toString());
		jsonArray = ((JSONObject) task.getResult()).getJSONArray("nearshops");
		mLv_shop.onRefreshComplete();
		if (jsonArray.length() > 0) {
			mLv_shop.setVisibility(View.VISIBLE);
			tv_tip.setVisibility(View.GONE);
			mLv_shop.setTotal(jsonArray.length());
			adapter.setJsonArray(jsonArray);
			adapter.notifyDataSetChanged();
		} else {
			mLv_shop.setVisibility(View.GONE);
			tv_tip.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onTaskFailed(SHTask task) {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		SHDialog.showOneKeyDialog(this, task.getRespInfo().getMessage(), null);
		mLv_shop.onRefreshComplete();
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
