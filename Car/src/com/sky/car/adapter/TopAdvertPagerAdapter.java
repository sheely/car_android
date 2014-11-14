package com.sky.car.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sky.base.SHContainerActivity;
import com.sky.car.R;

public class TopAdvertPagerAdapter extends PagerAdapter {

	private LayoutInflater mInflater;
	private HashMap<Integer, View> mCacheView = new HashMap<Integer, View>();
	private OnClickListener mItemClickListener;
	private Context mContext;
	private ArrayList<Integer> ivList;

	public TopAdvertPagerAdapter(Context context, ArrayList<Integer> list) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		this.ivList = list;
		mItemClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewHolder holder = (ViewHolder) v.getTag();
				int position = holder.position;
//				Intent intent = new Intent(mContext, SHContainerActivity.class);
//				intent.putExtra("class", "com.eroad.offer.home.OfferAdvFragment");
//				intent.putExtra("url", url);
//				intent.putExtra("title", holder.title);
//				mContext.startActivity(intent);
			}
		};
	}

	@Override
	public int getCount() {
		return ivList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if(mCacheView.containsKey(position)){
			View view = mCacheView.get(position);
			if(container.indexOfChild(view) != -1){
				container.removeView(view);
			}
		}
//		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		ViewHolder holder = null;

		View view = mInflater.inflate(R.layout.item_adv, null);
		holder = new ViewHolder();
		holder.iv = (ImageView) view.findViewById(R.id.iv_adv);
		holder.iv.setImageResource(ivList.get(position));
		view.setTag(holder);
		
		holder.position = position;

		view.setOnClickListener(mItemClickListener);
		container.addView(view);
		mCacheView.put(position, view);
		return view;

	}

	static class ViewHolder {
		ImageView iv;
		int position;
	}
}
