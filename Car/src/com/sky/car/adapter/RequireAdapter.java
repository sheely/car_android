package com.sky.car.adapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sky.car.R;
import com.sky.car.widget.SHImageView;

public class RequireAdapter extends BaseAdapter{
	private Context context;
	private JSONArray jsonArray = new JSONArray();
	
	public void setJsonArray(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	public RequireAdapter(Context c){
		this.context = c;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return jsonArray.length();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public class ViewHolder{
		public TextView tv_service,tv_require,tv_time;   
		private SHImageView iv_service;
	}
	
	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_require, null);
			holder = new ViewHolder();
			holder.tv_service = (TextView) convertView.findViewById(R.id.tv_service_type);
			holder.tv_require = (TextView) convertView.findViewById(R.id.tv_require);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.iv_service = (SHImageView) convertView.findViewById(R.id.iv_service_type);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		holder.tv_service.setText(jsonArray.optJSONObject(arg0).optString("problemdesc"));
		holder.tv_time.setText(jsonArray.optJSONObject(arg0).optString("asktime"));
		return convertView;
	}

}
