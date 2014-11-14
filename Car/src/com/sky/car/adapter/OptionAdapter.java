package com.sky.car.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sky.car.R;

public class OptionAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<String> list = new ArrayList<String>();
	private HashMap<String,Integer> map = new HashMap<String,Integer>();
	public OptionAdapter(Context c){
		this.context = c;
		list.add("保养");
		list.add("保养");
		list.add("保养");
		list.add("保养");
		list.add("保养");
		list.add("保养");
		list.add("紧急救援");
		map.put("selected", 0);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
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
	}
	
	@Override
	public View getView(final int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_option, null);
			holder = new ViewHolder();
			holder.tv_item = (TextView) convertView.findViewById(R.id.tv_item);
			holder.tv_item.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					map.put("selected", arg0);
					notifyDataSetChanged();
				}
			});
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		if(map.get("selected") == arg0){
			holder.tv_item.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_grid_option_selected));
		}else{
			holder.tv_item.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_grid_option_normal));
		}
		holder.tv_item.setText(list.get(arg0));
		return convertView;
	}

}
