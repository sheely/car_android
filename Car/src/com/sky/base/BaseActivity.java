package com.sky.base;

import java.lang.reflect.Method;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.next.dynamic.SHClass;




public class BaseActivity extends FragmentActivity {
	/**
	 * ��
	 */
	private ImageView testMarkImageView;
//	protected DetailTitlebar mNavigationBar;
	/**
	 * 
	 */
	private class SHPreModule{
		public String PreAction = "";
		public Intent PreIntent;
		public Context PreContext;
	}
	private static HashMap<String, SHPreModule> mMap = new HashMap<String, SHPreModule>();
	private BroadcastReceiver mBroadcastReceiver = null;
	private IntentFilter mIntentFilter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		mNavigationBar = (DetailTitlebar)this.findViewById(R.id.detailTitlebar);
		if (this.shouldShowTestMark()) {
//			if (!CommonUtil.isMainServer(this)) {
//				this.setTestInfoHidden(false);
//			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (testMarkImageView != null && testMarkImageView.getParent() != null) {
			WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
			wm.removeView(testMarkImageView);
			testMarkImageView = null;
		}
		
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			finish();
//		}
//		return false;
//	}

	protected boolean shouldShowTestMark() {
		return true;
	}

	

	public void startActivity(Intent intent) {
		if (intent.getExtras() != null
				&& intent.getExtras().containsKey("pre_action")) {
			String pro_action = intent.getExtras().getString("pre_action");
			String module = SHModuleManager.getInstance().getModuleNameByPreAction(pro_action);
			try {
				Class clazz = SHClass.getClass(module);
				Method m = clazz.getMethod("__Test_Pre_Action");
				Boolean object = (Boolean) m.invoke(null);
				if(object){
					super.startActivity(intent);
				}else{
					SHPreModule pm = new SHPreModule();
					pm.PreAction = pro_action;
					pm.PreContext = this;
					pm.PreIntent = intent;
					mMap.put(pro_action, pm);
					if(mIntentFilter == null){
						mIntentFilter = new IntentFilter();
					}
					mIntentFilter.addAction(pro_action);
					if(mBroadcastReceiver == null){
						mBroadcastReceiver=  new BroadcastReceiver() {
								@Override
								public void onReceive(Context context, Intent intent) {
									//处理回调
									SHPreModule pm = mMap.get(intent.getAction());
									if(pm != null){
										pm.PreContext.startActivity(pm.PreIntent);
									}
								}
							};
					}
					this.registerReceiver(mBroadcastReceiver, mIntentFilter);
					Intent intent_new = new Intent(this, SHContainerActivity.class);
					intent_new.putExtra("class", module);
					String per_action = SHModuleManager.getInstance().getNeed_Pre_Action_NameByTagert(module);
					if(per_action!= null && per_action.length() > 0){
						intent_new.putExtra("pre_action",per_action);
					}
					this.startActivity(intent_new);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		} else {
			super.startActivity(intent);
		}

	}

}
