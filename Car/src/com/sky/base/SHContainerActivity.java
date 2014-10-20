package com.sky.base;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import com.next.dynamic.SHClass;
import com.sky.car.R;

public class SHContainerActivity extends BaseActivity {
	Fragment fragment;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String str = this.getIntent().getStringExtra("class");

		LinearLayout line = new LinearLayout(this);
		// View view = new View(this);
		line.setId(1);

//		this.setContentView(line,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(R.layout.activity_sh);
		if(savedInstanceState == null){
			fragment = (Fragment)(SHClass.getInstance(str));
//			FragmentTransaction ft = this.getSupportFragmentManager()
//					.beginTransaction();
//			ft.add(1,fragment, "main");
//			ft.commit();
			getSupportFragmentManager().beginTransaction()
            .add(R.id.container_sh, fragment)
            .commit();
		}
//		SHApplication.getInstance().addActivity(this);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Boolean flag = false;
		if (fragment instanceof ISHKeyEvent) {
			flag = ((ISHKeyEvent) fragment).onKeyDown(keyCode, event);
		}
		if(!flag){
			return super.onKeyDown(keyCode, event);
		}
		return false;
	}

	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		Boolean flag = false;
		if (fragment instanceof ISHKeyEvent) {
			flag = ((ISHKeyEvent) fragment).onKeyLongPress(keyCode, event);
		}
		if(!flag){
			return super.onKeyDown(keyCode, event);
		}
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Boolean flag = false;
		if (fragment instanceof ISHKeyEvent) {
			flag = ((ISHKeyEvent) fragment).onKeyUp(keyCode, event);
		}
		if(!flag){
			return super.onKeyDown(keyCode, event);
		}
		return false;
	}

	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		Boolean flag = false;
		if (fragment instanceof ISHKeyEvent) {
			flag = ((ISHKeyEvent) fragment).onKeyMultiple(keyCode, repeatCount,
					event);
		}
		if(!flag){
			return super.onKeyDown(keyCode, event);
		}
		return false;
	}
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		fragment.onActivityResult(requestCode, resultCode, data);
////		super.onActivityResult(requestCode, resultCode, data);
//	}
}
