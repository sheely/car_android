package com.sky.car.home;

import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.sky.base.BaseFragment;
import com.sky.base.BaseNormalActivity;
import com.sky.base.SHContainerActivity;
import com.sky.car.R;
import com.sky.car.util.ConfigDefinition;
import com.sky.car.widget.SHImageView;
import com.sky.widget.SHDialog;
import com.sky.widget.SHDialog.DialogClickListener;

public class ShopDetailActivity extends BaseNormalActivity implements ITaskListener{

	private SHPostTaskM detailTask;
	private TextView mTv_shop_name,mTv_shop_address,mTv_score;
	private SHImageView mIv_logo;
	private RatingBar mRating;
	private Button mBtn_contact,mBtn_appraise,mBtn_daohang;
	JSONObject json;
//	@Override
//	public void onViewCreated(View view, Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onViewCreated(view, savedInstanceState);
//		mDetailTitlebar.setTitle("商户详情");
//		
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.fragment_shop_detail);
		super.onCreate(savedInstanceState);
		mDetailTitlebar.setTitle("商户详情");
		mTv_shop_name = (TextView) findViewById(R.id.tv_shopname);
		mTv_shop_address = (TextView) findViewById(R.id.tv_address);
		mTv_score = (TextView) findViewById(R.id.tv_special_price);
		mIv_logo = (SHImageView) findViewById(R.id.iv_shop);
		mRating = (RatingBar) findViewById(R.id.rating);
		mBtn_contact = (Button) findViewById(R.id.btn_contact);
		mBtn_appraise = (Button) findViewById(R.id.btn_appraise);
		mBtn_daohang = (Button) findViewById(R.id.btn_daohang);
		OnClick onClick = new OnClick();
		mBtn_contact.setOnClickListener(onClick);
		mBtn_appraise.setOnClickListener(onClick);
		mBtn_daohang.setOnClickListener(onClick);
		SHDialog.ShowProgressDiaolg(this, null);
		detailTask = new SHPostTaskM();
		detailTask.setUrl(ConfigDefinition.URL+"shopdetail.action");
		detailTask.setListener(this);
		detailTask.getTaskArgs().put("shopid", getIntent().getStringExtra("shopid"));
		detailTask.start();
	}

	private class OnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_contact:
				SHDialog.showDoubleKeyDialog(ShopDetailActivity.this, "拨打号码："+json.optString("shopmobile"), new DialogClickListener() {
					
					@Override
					public void confirm() {
						// TODO Auto-generated method stub
						Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+json.optString("shopmobile")));
					    startActivity(intent);
					}
					
					@Override
					public void cancel() {
						// TODO Auto-generated method stub
						
					}
				});
				break;
			case R.id.btn_appraise:
				break;
			case R.id.btn_daohang:
				Intent intent = new Intent(ShopDetailActivity.this,MapActivity.class);
				intent.putExtra("json", json.toString());
				startActivity(intent);
//				Intent intent = new Intent(getActivity(),TestMapActivity.class);
//				intent.putExtra("json", json.toString());
//				startActivity(intent);
				break;
			}
		}
		
	}
	
	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(task.getResult().toString());
		SHDialog.dismissProgressDiaolg();
		json = (JSONObject) task.getResult();
		mTv_shop_name.setText(json.optString("shopname"));
		mTv_shop_address.setText(json.optString("shopaddress"));
		mRating.setRating(Float.valueOf(json.optString("shopscore").toString()));
		mTv_score.setText(""+json.optString("shopscore")+"分");
		mIv_logo.setNewImgForImageSrc(true);
		mIv_logo.setURL(json.optString("shoplogo"));
	}

	@Override
	public void onTaskFailed(SHTask task) {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		SHDialog.showOneKeyDialog(ShopDetailActivity.this, task.getRespInfo().getMessage(), null);
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
