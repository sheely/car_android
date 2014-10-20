package com.sky.car.adapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sky.car.R;
import com.sky.car.widget.SHImageView;

public class DashAdapter extends BaseAdapter{
	private Context context;
	private JSONArray jsonArray = new JSONArray();
	public void setData(JSONArray arr){
		this.jsonArray = arr;
	}
	public DashAdapter(Context c){
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
		public TextView tv_item; 
		public SHImageView iv_logo;
		public ImageView iv_status;
	}
	
	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_dash, null);
			holder = new ViewHolder();
			holder.tv_item = (TextView) convertView.findViewById(R.id.tv_item);
			holder.iv_logo = (SHImageView) convertView.findViewById(R.id.iv_dash_logo);
			holder.iv_status = (ImageView) convertView.findViewById(R.id.iv_status);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		try {
			holder.tv_item.setText(jsonArray.getJSONObject(arg0).getString("devicename"));
			holder.iv_logo.setNewImgForImageSrc(true);
			holder.iv_logo.setURL(jsonArray.getJSONObject(arg0).getString("deviceslogo"));
			switch(jsonArray.getJSONObject(arg0).getInt("devicestatus")){
			case 0:
				holder.iv_status.setImageResource(R.drawable.img_good);
				break;
			case 1:
				holder.iv_status.setImageResource(R.drawable.img_warn);
				break;
			case 2:
				holder.iv_status.setImageResource(R.drawable.img_wrong);
				break;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertView;
	}

}
