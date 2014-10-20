package com.sky.base;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sky.car.R;

public class DetailTitlebar extends RelativeLayout {
	private TextView titleText;
	private TextView titleNumber;
	private TextView backButton;
	private TextView rightButton;
	private LinearLayout ll_back;
	private RelativeLayout rl_right;
	
	public DetailTitlebar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DetailTitlebar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DetailTitlebar(Context context) {
		super(context);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		backButton = (TextView) findViewById(R.id.back_button);
		ll_back = (LinearLayout) findViewById(R.id.ll_back);
		titleText = (TextView) findViewById(android.R.id.title);
//		titleNumber = (TextView) findViewById(R.id.number);
		rightButton = (TextView) findViewById(R.id.right_button);
		rl_right = (RelativeLayout) findViewById(R.id.rl_right);
//		this.rightCustomView = (LinearLayout)findViewById(R.id.rightCustomView);
//		this.ivSeperator = (ImageView)findViewById(R.id.ivSeperator);
		
//		this.rightCustomView.setVisibility(View.GONE);
//		rightButton.setVisibility(View.INVISIBLE);
	}

	
	public void setTitle(CharSequence title) {
		if (titleText != null)
			titleText.setText(title);
	}

	public void setTitleNumber(CharSequence number) {
		if (titleNumber != null){
			titleNumber.setVisibility(View.VISIBLE);
			titleNumber.setText(number);
		}
	}

	public void setRightButton(String text, OnClickListener listener) {
		rightButton.setText(text);
		rl_right.setOnClickListener(listener);
		rightButton.setVisibility(View.VISIBLE);
	}
	public void setRightButton(int res, OnClickListener listener) {
		rl_right.setOnClickListener(listener);
		rightButton.setVisibility(View.VISIBLE);
		rightButton.setBackgroundResource(res);
	}
	public void setLeftButton(String text, OnClickListener listener) {
		backButton.setBackgroundResource(R.color.full_transparent);
		backButton.setText(text);
		ll_back.setOnClickListener(listener);
		backButton.setVisibility(View.VISIBLE);
	}
	public void setLeftButton(int res, OnClickListener listener) {
		ll_back.setOnClickListener(listener);
		backButton.setBackgroundResource(res);
		backButton.setVisibility(View.VISIBLE);
	}

	public TextView getBackButton() {
		return backButton;
	}

	public TextView getRightButton() {
		return rightButton;
	}
	
//	public void setRightCustomeView(View view) {
//		if (view == null) {
//			return;
//		}
//		rightCustomView.removeAllViews();
//		this.rightCustomView.addView(view);
//		this.rightCustomView.setVisibility(View.VISIBLE);
//	}
	
//	public void setShowSeperator(boolean show) {
//		if (show) {
//			this.ivSeperator.setVisibility(View.VISIBLE);
//		} else {
//			this.ivSeperator.setVisibility(View.GONE);
//		}
//	}
}
