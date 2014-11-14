package com.sky.car;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.sky.car.contact.ContactFragment;
import com.sky.car.document.DocumentFragment;
import com.sky.car.home.HomeFragment;
import com.sky.car.myself.MyselfFragment;
import com.sky.car.util.SHLocationManager;
import com.sky.widget.SHDialog;
import com.sky.widget.SHDialog.DialogClickListener;

public class MainActivity extends FragmentActivity {

	private RadioGroup rg_main;
	private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_main = (RadioGroup) findViewById(R.id.rg_main);
        changeFragment("home");
        rg_main.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch(group.getCheckedRadioButtonId()){
				case R.id.rb_0:
					changeFragment("home");
					break;
				case R.id.rb_1:
					changeFragment("document");
					break;
				case R.id.rb_2:
					changeFragment("contact");
					break;
				case R.id.rb_3:
					changeFragment("myself");
					break;
				}
			}
		});
    }


    /**
	 * 根据tag切换fragment
	 * 
	 * @param tag
	 */
	public void changeFragment(String tag) {

		hideFragment();
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);

		if (fragment != null) {
			transaction.show(fragment);
		} else {
			if (tag.equals("home")) {
				fragment = new HomeFragment();
			} else if (tag.equals("document")) {
				fragment = new DocumentFragment();
			} else if (tag.equals("myself")) {
				fragment = new MyselfFragment();
			} else if (tag.equals("contact")) {
				fragment = new ContactFragment();
			} 
			mFragmentList.add(fragment);
			transaction.add(R.id.container, fragment, tag);
		}

		transaction.commitAllowingStateLoss();
	}

	/**
	 * 隐藏所有fragment
	 */
	private void hideFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		for (Fragment f : mFragmentList) {
			ft.hide(f);
		}
		ft.commitAllowingStateLoss();
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		SHDialog.showDoubleKeyDialog(this,"车宝宝", "是否退出当前应用？", new DialogClickListener() {
			
			@Override
			public void confirm() {
				// TODO Auto-generated method stub
				android.os.Process.killProcess(android.os.Process.myPid());
			}
			
			@Override
			public void cancel() {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
