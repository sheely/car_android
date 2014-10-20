package com.sky.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;

import com.sky.car.R;
import com.sky.widget.SHDialog;

public class BaseFragment extends Fragment implements ISHKeyEvent {

	protected DetailTitlebar mDetailTitlebar;
	private int index;

	public int getIndex() {
		return index;
	}

	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {

		return false;
	}

	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		return false;

	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SHDialog.dismissProgressDiaolg();
			getActivity().finish();
		}
		return true;
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mDetailTitlebar = (DetailTitlebar) view.findViewById(R.id.detailTitlebar);
		if (mDetailTitlebar != null) {
			this.setNabiagtionBar();
		}
	}

	/**
	 * 
	 */
	protected void setNabiagtionBar() {
		mDetailTitlebar.setLeftButton(R.drawable.ic_back, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
			}
		});
	}
}
