package com.sky.car.document;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.sky.base.BaseFragment;
import com.sky.base.SHContainerActivity;
import com.sky.car.R;
import com.sky.car.adapter.DashAdapter;
import com.sky.car.util.ConfigDefinition;
import com.sky.car.widget.RoundProgressBar;
import com.sky.car.widget.SHImageView;
import com.sky.widget.SHDialog;
import com.sky.widget.SHDialog.DialogClickListener;

@SuppressLint("NewApi")
public class DocumentFragment extends BaseFragment implements ITaskListener {

	private SHImageView iv_car_logo;
	private TextView mTv_car_xilie, mTv_car_number, mTv_score,mTv_current_option;
	private ImageView mIv_zhizhen, mIv_yibiao, mIv_1;
	private SHImageView mIv_car;
	private SHPostTaskM dashTask;
	// private RelativeLayout[] rl = new RelativeLayout[4];
	private GridView mGv_dash;
	private DashAdapter adapter;
	private PopupWindow pop;
	private LinearLayout mLl_stimulus, mLl_operate, mLl_economy, mLl_safe;
	private Animation animation1;
	private RoundProgressBar mRoundProgressBar1, mRoundProgressBar2,
			mRoundProgressBar3, mRoundProgressBar4,mRoundProgressBar_current;
	private int progress = 0;
	private TextView[] tv = new TextView[4];
	private RelativeLayout mRl_main;
	private LinearLayout mLl_item;
	private Button mBtn_back;
	private JSONArray jsonArray;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mDetailTitlebar.setTitle("车况档案");
		mDetailTitlebar.setLeftButton(null, null);
		mDetailTitlebar.setRightButton("分享", new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showShare();
			}
		});

		iv_car_logo = (SHImageView) view.findViewById(R.id.iv_car_logo);
		mTv_car_xilie = (TextView) view.findViewById(R.id.tv_car_xilie);
		mTv_car_number = (TextView) view.findViewById(R.id.tv_car_number);
		mIv_car = (SHImageView) view.findViewById(R.id.iv_car_logo);
		mIv_zhizhen = (ImageView) view.findViewById(R.id.iv_zhizhen);
		mIv_yibiao = (ImageView) view.findViewById(R.id.iv_yibiao);
		mTv_score = (TextView) view.findViewById(R.id.tv_score);
		mIv_1 = (ImageView) view.findViewById(R.id.iv_new);
		tv[0] = (TextView) view.findViewById(R.id.tv_check_report);
		tv[1] = (TextView) view.findViewById(R.id.tv_repair);
		tv[2] = (TextView) view.findViewById(R.id.tv_insurance);
		tv[3] = (TextView) view.findViewById(R.id.tv_condition);
		mTv_current_option = (TextView) view.findViewById(R.id.tv_current_option);
		mRl_main = (RelativeLayout) view.findViewById(R.id.rl_main);
		mLl_item = (LinearLayout) view.findViewById(R.id.ll_item);
		mGv_dash = (GridView) view.findViewById(R.id.gv_dash);
		mGv_dash.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				showOptionPop(view);
			}
		});
		OnClick onClick = new OnClick();
		tv[0].setOnClickListener(onClick);
		tv[1].setOnClickListener(onClick);
		tv[2].setOnClickListener(onClick);
		tv[3].setOnClickListener(onClick);
		JSONObject json;
		try {
			json = new JSONObject(getActivity().getIntent().getStringExtra(
					"car"));
			iv_car_logo.setNewImgForImageSrc(true);
			iv_car_logo.setURL(json.getString("carlogo"));
			mTv_car_xilie.setText(json.getString("carcategoryname")
					+ json.getString("carseriesname"));
			mTv_car_number.setText(json.getString("carcardno"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		requestDash();
		requestNotify();
		mRoundProgressBar1 = (RoundProgressBar) view
				.findViewById(R.id.roundProgressBar1);
		mRoundProgressBar2 = (RoundProgressBar) view
				.findViewById(R.id.roundProgressBar2);
		mRoundProgressBar3 = (RoundProgressBar) view
				.findViewById(R.id.roundProgressBar3);
		mRoundProgressBar4 = (RoundProgressBar) view
				.findViewById(R.id.roundProgressBar4);
		mRoundProgressBar_current = (RoundProgressBar) view.findViewById(R.id.roundProgressBar_current);
		mBtn_back = (Button) view.findViewById(R.id.btn_back);
		mBtn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mLl_item.setVisibility(View.GONE);
				mRl_main.setVisibility(View.VISIBLE);
			}
		});
		mLl_stimulus = (LinearLayout) view.findViewById(R.id.ll_stimulus);
		mLl_operate = (LinearLayout) view.findViewById(R.id.ll_operate);
		mLl_safe = (LinearLayout) view.findViewById(R.id.ll_safe);
		mLl_economy = (LinearLayout) view.findViewById(R.id.ll_economy);
		mLl_stimulus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mRl_main.setVisibility(View.GONE);
				mLl_item.setVisibility(View.VISIBLE);
				mTv_current_option.setText("动力性");
				Drawable topDrawable = getResources().getDrawable(R.drawable.icon_power);  
				topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
				mTv_current_option.setCompoundDrawables(null, topDrawable, null, null);
				if(adapter == null){
					adapter = new DashAdapter(getActivity());
					mGv_dash.setAdapter(adapter);
				}
				adapter.setData(jsonArray.optJSONObject(0).optJSONArray("deviceentities"));
				adapter.notifyDataSetChanged();
			}
		});
		mLl_safe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mRl_main.setVisibility(View.GONE);
				mLl_item.setVisibility(View.VISIBLE);
				mTv_current_option.setText("安全性");
				Drawable topDrawable = getResources().getDrawable(R.drawable.icon_safety);  
				topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
				mTv_current_option.setCompoundDrawables(null, topDrawable, null, null);
				if(adapter == null){
					adapter = new DashAdapter(getActivity());
					mGv_dash.setAdapter(adapter);
				}
				adapter.setData(jsonArray.optJSONObject(1).optJSONArray("deviceentities"));
				adapter.notifyDataSetChanged();
			}
		});
		mLl_economy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mRl_main.setVisibility(View.GONE);
				mLl_item.setVisibility(View.VISIBLE);
				mTv_current_option.setText("经济性");
				Drawable topDrawable = getResources().getDrawable(R.drawable.icon_oil);  
				topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
				mTv_current_option.setCompoundDrawables(null, topDrawable, null, null);
				if(adapter == null){
					adapter = new DashAdapter(getActivity());
					mGv_dash.setAdapter(adapter);
				}
				adapter.setData(jsonArray.optJSONObject(2).optJSONArray("deviceentities"));
				adapter.notifyDataSetChanged();
			}
		});
		mLl_operate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mRl_main.setVisibility(View.GONE);
				mLl_item.setVisibility(View.VISIBLE);
				mTv_current_option.setText("操控性");
				Drawable topDrawable = getResources().getDrawable(R.drawable.icon_controller);  
				topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
				mTv_current_option.setCompoundDrawables(null, topDrawable, null, null);
				if(adapter == null){
					adapter = new DashAdapter(getActivity());
					mGv_dash.setAdapter(adapter);
				}
				adapter.setData(jsonArray.optJSONObject(3).optJSONArray("deviceentities"));
				adapter.notifyDataSetChanged();
			}
		});
	}
  
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_document, container,
				false);
		return view;
	}

	private void startSpreadAnim1() {
		if (animation1 == null) {
			animation1 = AnimationUtils.loadAnimation(getActivity(),
					R.anim.anim_spread1);
		}
		AnimationSet set = new AnimationSet(true);
		set.setInterpolator(new OvershootInterpolator());
		set.addAnimation(animation1);
		set.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				mLl_economy.setVisibility(View.VISIBLE);
				mLl_stimulus.setVisibility(View.VISIBLE);
				mLl_operate.setVisibility(View.VISIBLE);
				mLl_safe.setVisibility(View.VISIBLE);
				new Thread(new Runnable() {

					@Override
					public void run() {
						while (progress < 90) {
							progress += 2;

							mRoundProgressBar1.setProgress(progress);
							mRoundProgressBar2.setProgress(progress);
							mRoundProgressBar3.setProgress(progress);
							mRoundProgressBar4.setProgress(progress);

							try {
								Thread.sleep(40);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}

					}
				}).start();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub

			}
		});
		mLl_operate.startAnimation(set);
		mLl_safe.startAnimation(set);
		mLl_economy.startAnimation(set);
		mLl_stimulus.startAnimation(set);

	}

	private void showOptionPop(View view) {
		if (pop == null) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View contentView = inflater.inflate(R.layout.pop_dash, null);
			pop = new PopupWindow(contentView, 480,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			TextView tv = (TextView) contentView.findViewById(R.id.tv_normal);
			tv.setOnClickListener(mPopClickListener);
			tv = (TextView) contentView.findViewById(R.id.tv_warn);
			tv.setOnClickListener(mPopClickListener);
			tv = (TextView) contentView.findViewById(R.id.tv_wrong);
			tv.setOnClickListener(mPopClickListener);
			tv = (TextView) contentView.findViewById(R.id.tv_report);
			tv.setOnClickListener(mPopClickListener);
			pop.setBackgroundDrawable(getResources().getDrawable(
					R.color.full_transparent));
			pop.setTouchable(true);
			pop.setOutsideTouchable(true);
			pop.setFocusable(true);
			pop.update();
		}
		int[] location = new int[2];
		// ((TextView)
		// mGv_dash.getItemAtPosition(position)).getLocationInWindow(location);
		view.getLocationInWindow(location);
		pop.setAnimationStyle(R.style.TypeSelAnimationFade);
		pop.showAtLocation(getActivity().getWindow().getDecorView(),
				Gravity.NO_GRAVITY,
				location[0] + view.getWidth() / 2 - pop.getWidth() / 2,
				location[1] + view.getHeight());
	}

	private View.OnClickListener mPopClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (pop != null && pop.isShowing()) {
				pop.dismiss();
			}
			switch (arg0.getId()) {
			case R.id.tv_normal:
				SHDialog.showDoubleKeyDialog(getActivity(), "确定变更状态？",
						new DialogClickListener() {

							@Override
							public void confirm() {
								// TODO Auto-generated method stub

							}

							@Override
							public void cancel() {
								// TODO Auto-generated method stub

							}
						});
				break;
			case R.id.tv_warn:
				SHDialog.showDoubleKeyDialog(getActivity(), "确定变更状态？",
						new DialogClickListener() {

							@Override
							public void confirm() {
								// TODO Auto-generated method stub

							}

							@Override
							public void cancel() {
								// TODO Auto-generated method stub

							}
						});
				break;
			case R.id.tv_wrong:
				SHDialog.showDoubleKeyDialog(getActivity(), "确定变更状态？",
						new DialogClickListener() {

							@Override
							public void confirm() {
								// TODO Auto-generated method stub

							}

							@Override
							public void cancel() {
								// TODO Auto-generated method stub

							}
						});
				break;
			case R.id.tv_report:
				break;
			}
		}

	};

	private class OnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.tv_check_report:
				Intent intent0 = new Intent(getActivity(),
						SHContainerActivity.class);
				intent0.putExtra("class", CheckReportFragment.class.getName());
				startActivity(intent0);
				break;
			case R.id.tv_repair:
				Intent intent1 = new Intent(getActivity(),
						SHContainerActivity.class);
				intent1.putExtra("class", RepairReportFragment.class.getName());
				startActivity(intent1);
				break;
			case R.id.tv_insurance:
				Intent intent2 = new Intent(getActivity(),
						SHContainerActivity.class);
				intent2.putExtra("class", InsuranceOverFragment.class.getName());
				startActivity(intent2);
				break;
			case R.id.tv_condition:
				Intent intent3 = new Intent(getActivity(),
						SHContainerActivity.class);
				intent3.putExtra("class", StatusChangeFragment.class.getName());
				startActivity(intent3);
				break;
			}
		}

	}

	private void requestDash() {
		SHDialog.ShowProgressDiaolg(getActivity(), null);
		dashTask = new SHPostTaskM();
		dashTask.setUrl(ConfigDefinition.URL + "dashboard.action");
		dashTask.setListener(this);
		dashTask.start();
	}

	private void requestNotify() {
		SHPostTaskM task1 = new SHPostTaskM();
		task1.setUrl(ConfigDefinition.URL + "checkreportnotify.action");
		task1.setListener(this);
		task1.start();
	}

	private void showShare() {
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.ic_launcher,
				getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("content");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("http://www.baidu.com/img/bd_logo1.png");
		oks.setImageUrl("http://www.baidu.com/img/bd_logo1.png");
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("test");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		// oks.setSiteUrl("http://sharesdk.cn");

		// 启动分享GUI
		oks.show(getActivity());
	}

	private void startAnim(final int score) {
		RotateAnimation anim = new RotateAnimation(0f, 220f,
				Animation.RELATIVE_TO_SELF, 0.88f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setDuration(3 * 220);// 每度3毫秒
		anim.setFillAfter(true);
		anim.setInterpolator(new Interpolator() {

			@Override
			public float getInterpolation(float input) {
				// TODO Auto-generated method stub
				if (input > 3 * 133.33 / (3 * 220)) {
					mIv_yibiao.setImageDrawable(getActivity().getResources()
							.getDrawable(R.drawable.dashboard_normal));
					return input;
				}
				if (input > 3 * 46.67 / (3 * 220)) {
					mIv_yibiao.setImageDrawable(getActivity().getResources()
							.getDrawable(R.drawable.dashboard_fault));
					return input;
				}

				return input;
			}
		});
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				// mIv_zhizhen.clearAnimation();
				float temp = Float.valueOf(score + "");
				float degree = Float.valueOf(((temp / 100) * 260 - 40));
				RotateAnimation anim = new RotateAnimation(220f, degree,
						Animation.RELATIVE_TO_SELF, 0.88f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				anim.setDuration(5 * (220 - (int) degree));
				anim.setFillAfter(true);
				// anim.setInterpolator(new Interpolator() {
				//
				// @Override
				// public float getInterpolation(float input) {
				// // TODO Auto-generated method stub
				// if(input > 5*173.33/(5*220)){
				// mIv_yibiao.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.dashboard_warning));
				// mTv_score.setTextColor(getResources().getColor(R.color.red));
				// return input;
				// }
				// if(input > 5*86.67/(5*220)){
				// mIv_yibiao.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.dashboard_fault));
				// mTv_score.setTextColor(getResources().getColor(R.color.orange));
				// return input;
				// }
				// return input;
				// }
				// });
				anim.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						// TODO Auto-generated method stub
						// mIv_zhizhen.clearAnimation();
						if (score > 66) {
							mIv_yibiao.setImageDrawable(getActivity()
									.getResources().getDrawable(
											R.drawable.dashboard_normal));
							mTv_score.setTextColor(getResources().getColor(
									R.color.green));
						} else if (score > 33) {
							mIv_yibiao.setImageDrawable(getActivity()
									.getResources().getDrawable(
											R.drawable.dashboard_fault));
							mTv_score.setTextColor(getResources().getColor(
									R.color.orange));
						} else {
							mIv_yibiao.setImageDrawable(getActivity()
									.getResources().getDrawable(
											R.drawable.dashboard_warning));
							mTv_score.setTextColor(getResources().getColor(
									R.color.red));
						}
						mTv_score.setText(score + "分");
					}
				});
				mIv_zhizhen.setAnimation(anim);
				anim.startNow();
			}
		});
		// mIv_zhizhen.setAnimation(anim);
		// anim.startNow();
		mIv_zhizhen.startAnimation(anim);
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		System.out.println(task.getResult().toString());
		JSONObject json = (JSONObject) task.getResult();
		if (task == dashTask) {
			JSONObject jsonCar = json.getJSONObject("activitedcar");
			mTv_car_xilie.setText(jsonCar.optString("carcategoryname")
					+ jsonCar.optString("carseriesname"));
			mTv_car_number.setText(jsonCar.optString("carcardno"));
			mIv_car.setNewImgForImageSrc(true);
			mIv_car.setURL(jsonCar.optString("carlogo"));
			int checkScore = json.optInt("totalscore");
//			JSONArray array = json.getJSONArray("deviceentities");
//			adapter = new DashAdapter(getActivity());
//			adapter.setData(array);
//			mGv_dash.setAdapter(adapter);
			// Message msg = new Message();
			// msg.what = checkScore;
			// handler.sendMessage(msg);
			jsonArray = json.optJSONArray("fourcategory");
			startAnim(checkScore);
			startSpreadAnim1();
		} else {
			if (json.optInt("iahasnew") == 1) {
				mIv_1.setVisibility(View.VISIBLE);
			} else {
				mIv_1.setVisibility(View.INVISIBLE);
			}
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			startAnim(msg.what);
		}

	};

	@Override
	public void onTaskFailed(SHTask task) {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		SHDialog.showOneKeyDialog(getActivity(), task.getRespInfo()
				.getMessage(), null);
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
