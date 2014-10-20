package com.sky.car;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * 
 * 
 * @author skypan
 */

public class WelcomeActivity extends Activity {

	private Runnable mEnterAppRunnable;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		mEnterAppRunnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		};
		mHandler = new Handler();
		mHandler.postDelayed(mEnterAppRunnable, 3000);
	}
}
