package com.sky.base;

import com.sky.car.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class BaseNormalActivity extends Activity {

	protected DetailTitlebar mDetailTitlebar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mDetailTitlebar = (DetailTitlebar)findViewById(R.id.detailTitlebar);
		if (mDetailTitlebar != null) {
			this.setNabiagtionBar();
		}
	}
	protected void setNabiagtionBar() {
		mDetailTitlebar.setLeftButton(R.drawable.ic_back, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
