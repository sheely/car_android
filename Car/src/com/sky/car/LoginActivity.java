package com.sky.car;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.next.app.StandardApplication;
import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.next.util.SHEnvironment;
import com.sky.base.BaseActivity;
import com.sky.base.DetailTitlebar;
import com.sky.base.SHApplication;
import com.sky.car.util.ConfigDefinition;
import com.sky.car.util.UserInfoManager;
import com.sky.widget.SHDialog;
import com.sky.widget.SHToast;

public class LoginActivity extends BaseActivity implements ITaskListener{

	private Button btn_login,btn_getcode;
	private SHPostTaskM getCodeTask,loginTask;
	private EditText et_phone,et_code;
	@Override
	protected void onCreate(Bundle savedInstanceState) {  
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		DetailTitlebar titlebar = (DetailTitlebar) findViewById(R.id.detailTitlebar);
		titlebar.setTitle("用户登录");
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_getcode = (Button) findViewById(R.id.btn_getcode);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_code = (EditText) findViewById(R.id.et_code);
		et_phone.setText(UserInfoManager.getInstance().getName());
		et_code.setText(UserInfoManager.getInstance().getPassword());
		btn_getcode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String phone = et_phone.getText().toString().trim();
				if(phone == null || phone.length() <= 0){
					SHToast.showToast(LoginActivity.this, "手机号不能为空！", 1000);
					return;
				}
				SHDialog.ShowProgressDiaolg(LoginActivity.this, null);
				getCodeTask = new SHPostTaskM();
				getCodeTask.setUrl(ConfigDefinition.URL+"smssend.action");
				getCodeTask.setListener(LoginActivity.this);
				SHEnvironment.getInstance().setLoginId(phone);
				getCodeTask.start();
			}
		});
		btn_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String phone = et_phone.getText().toString().trim();
				String code = et_code.getText().toString().trim();
				if(phone == null || phone.length() <= 0 || code == null || code.length() <= 0){
					SHToast.showToast(LoginActivity.this, "手机号或验证码不能为空！", 1000);
					return;
				}
				SHDialog.ShowProgressDiaolg(LoginActivity.this, null);
				loginTask = new SHPostTaskM();
				loginTask.setUrl(ConfigDefinition.URL+"login.action");
				SHEnvironment.getInstance().setLoginId(phone);
				SHEnvironment.getInstance().setPassword(code);
				loginTask.getTaskArgs().put("appuuid", "germmy20140710");
				loginTask.setListener(LoginActivity.this);
				loginTask.start();
				
			}
		});
	}
	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		System.out.println(task.getResult().toString());
		if(task == getCodeTask){
			
		}else if(task == loginTask){
			JSONObject json = ((JSONObject) task.getResult()).getJSONObject("activitedcar");
			UserInfoManager.getInstance().setName(et_phone.getText().toString().trim());
			UserInfoManager.getInstance().setPassword(et_code.getText().toString().trim());
			UserInfoManager.getInstance().sync(LoginActivity.this, true);
			Intent intent = new Intent(LoginActivity.this,MainActivity.class);
			intent.putExtra("car", json.toString());
			startActivity(intent);
			finish();
		}
	}
	@Override
	public void onTaskFailed(SHTask task) {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		SHDialog.showOneKeyDialog(LoginActivity.this, task.getRespInfo().getMessage(), null);
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
