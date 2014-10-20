package com.sky.car.adapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sky.car.R;
import com.sky.car.widget.SHImageView;

public class BrandAdapter extends BaseAdapter{
	private Context context;
	private JSONArray jsonArray = new JSONArray();
	
	public void setData(JSONArray arr){
		this.jsonArray = arr;
	}
	
	public BrandAdapter(Context c){
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
		public TextView tv_name,tv_xilie;   
		private SHImageView iv_car;
		private ImageView iv_selected;
	}
	
	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_cars, null);
			holder = new ViewHolder();
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_carname);
			holder.tv_xilie = (TextView) convertView.findViewById(R.id.tv_xilie);
			holder.iv_car = (SHImageView) convertView.findViewById(R.id.iv_car); 
			holder.iv_selected = (ImageView) convertView.findViewById(R.id.iv_selected);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
			holder.iv_car.setNewImgForImageSrc(true);
			try {
				System.out.println(jsonArray.getJSONObject(arg0).getString("carlogo"));
				holder.iv_car.setURL(jsonArray.getJSONObject(arg0).getString("carlogo"));
				holder.tv_name.setText(jsonArray.getJSONObject(arg0).getString("carcategoryname"));
				holder.tv_xilie.setText(jsonArray.getJSONObject(arg0).getString("carseriesname"));
				if(jsonArray.getJSONObject(arg0).optInt("isactivited") == 1){
					holder.iv_selected.setVisibility(View.VISIBLE);
				}else{
					holder.iv_selected.setVisibility(View.INVISIBLE);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return convertView;
	}

}
