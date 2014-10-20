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

public class ShopAdapter extends BaseAdapter{
	private Context context;
	private JSONArray jsonArray = new JSONArray();
	
	public void setJsonArray(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	public ShopAdapter(Context c){
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
		public TextView tv_name,tv_normal,tv_special,tv_address,tv_score,tv_distance;   
		private SHImageView iv_shop;
	}
	
	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_shop, null);
			holder = new ViewHolder();
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_shopname);
			holder.tv_normal = (TextView) convertView.findViewById(R.id.tv_normal_price);
			holder.tv_special = (TextView) convertView.findViewById(R.id.tv_special_price);
			holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
			holder.tv_score = (TextView) convertView.findViewById(R.id.tv_score);
			holder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
			holder.iv_shop = (SHImageView) convertView.findViewById(R.id.iv_shop);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
			holder.tv_normal.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			holder.iv_shop.setNewImgForImageSrc(true);
			try {
				holder.iv_shop.setURL(jsonArray.getJSONObject(arg0).getString("shoplogo"));
				holder.tv_name.setText(jsonArray.getJSONObject(arg0).getString("shopname"));
				holder.tv_address.setText(jsonArray.getJSONObject(arg0).getString("shopaddress"));
				holder.tv_normal.setText("普洗:"+jsonArray.getJSONObject(arg0).getString("normalwashoriginalprice")+"元");
				holder.tv_special.setText("特价:"+jsonArray.getJSONObject(arg0).getString("specialwashoriginalprice")+"元");
				holder.tv_score.setText(""+jsonArray.getJSONObject(arg0).getDouble("shopscore")+"分");
				holder.tv_distance.setText("距离:"+jsonArray.getJSONObject(arg0).getString("distancefromme"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return convertView;
	}

}
