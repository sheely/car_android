package com.sky.car.contact;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.sky.base.BaseFragment;
import com.sky.car.R;

public class ContactFragment extends BaseFragment {

	private RadioGroup rg;
	private List<Fragment> mFragmentList = new ArrayList<Fragment>();
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		rg = (RadioGroup) view.findViewById(R.id.rg);
		changeFragment("require");
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch(group.getCheckedRadioButtonId()){
				case R.id.rb_0:
					changeFragment("require");
					break;
				case R.id.rb_1:
					changeFragment("order");
					break;
				}
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_contact, container, false);
		return view;
	}

	/**
	 * 根据tag切换fragment
	 * 
	 * @param tag
	 */
	public void changeFragment(String tag) {
		hideFragment();
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);

		if (fragment != null) {
			transaction.show(fragment);
		} else {
			if (tag.equals("require")) {
				fragment = new RequirementFragment();
			} else if (tag.equals("order")) {
				fragment = new OrderFragment();
			} 
			mFragmentList.add(fragment);
			transaction.add(R.id.frame, fragment, tag);
		}

		transaction.commit();
	}

	/**
	 * 隐藏所有fragment
	 */
	private void hideFragment() {
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		for (Fragment f : mFragmentList) {
			ft.hide(f);
		}  
		ft.commit();
	}
}
