package com.sky.car.adapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sky.car.R;
import com.sky.car.widget.SHImageView;

public class OrderAdapter extends BaseExpandableListAdapter {
	private Context context;
	private JSONArray jsonArray = new JSONArray();

	public void setData(JSONArray arr) {
		this.jsonArray = arr;
	}

	public OrderAdapter(Context c) {
		this.context = c;
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	public class ChildViewHolder {
		private TextView tv_money, tv_shop_name;
		private SHImageView iv_shop;
		private Button btn_pay;
	}
	
	@Override
	public View getChildView(int arg0, int arg1, boolean arg2, View convertView,
			ViewGroup arg4) {
		// TODO Auto-generated method stub
		ChildViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_order_child, null);
			holder = new ChildViewHolder();
			holder.btn_pay = (Button) convertView.findViewById(R.id.btn_pay);
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
			holder.tv_shop_name = (TextView) convertView.findViewById(R.id.tv_shop_name);
			holder.iv_shop = (SHImageView) convertView.findViewById(R.id.iv_shop);
			convertView.setTag(holder);
		}else{
			holder=(ChildViewHolder)convertView.getTag();
		}
		holder.iv_shop.setNewImgForImageSrc(true);
		holder.iv_shop.setURL(jsonArray.optJSONObject(arg0).optJSONArray("baojialist").optJSONObject(arg1).optString("shoplogo"));
		holder.tv_money.setText(jsonArray.optJSONObject(arg0).optJSONArray("baojialist").optJSONObject(arg1).optString("discountafteronlinepay"));
		holder.tv_shop_name.setText(jsonArray.optJSONObject(arg0).optJSONArray("baojialist").optJSONObject(arg1).optString("shopname"));
		return convertView;
	}

	@Override
	public int getChildrenCount(int arg0) {
		// TODO Auto-generated method stub
		return jsonArray.optJSONObject(arg0).optJSONArray("baojialist").length();
	}

	@Override
	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return jsonArray.length();
	}

	@Override
	public long getGroupId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public class ParentViewHolder {
		private TextView tv_type, tv_chepai,tv_if_pay;
		private SHImageView iv_service;
		private ImageView iv_arrow;
	}

	@Override
	public View getGroupView(int arg0, boolean arg1, View convertView, ViewGroup arg3) {
		// TODO Auto-generated method stub
		ParentViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_order_parent, null);
			holder = new ParentViewHolder();
			holder.iv_arrow = (ImageView) convertView.findViewById(R.id.iv_arrow);
			holder.iv_service = (SHImageView) convertView.findViewById(R.id.iv_service_type);
			holder.tv_chepai = (TextView) convertView.findViewById(R.id.tv_chepai);
			holder.tv_if_pay = (TextView) convertView.findViewById(R.id.tv_if_fukuan);
			holder.tv_type = (TextView) convertView.findViewById(R.id.tv_service_type);
			convertView.setTag(holder);
		}else{
			holder=(ParentViewHolder)convertView.getTag();
		}
		if(arg1){
			holder.iv_arrow.setImageResource(R.drawable.ic_arrow_up);
		}else{
			holder.iv_arrow.setImageResource(R.drawable.ic_arrow_down);
		}
		holder.iv_service.setNewImgForImageSrc(true);
		try {
			holder.iv_service.setURL(jsonArray.getJSONObject(arg0).optString("servicecategorylogo"));
			holder.tv_chepai.setText(jsonArray.getJSONObject(arg0).optString("carno"));
			holder.tv_type.setText(jsonArray.getJSONObject(arg0).optString("servicecategoryname"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

}
