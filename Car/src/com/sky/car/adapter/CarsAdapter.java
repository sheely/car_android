package com.sky.car.adapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.sky.base.BaseFragment;
import com.sky.car.R;
import com.sky.car.home.HomeFragment;
import com.sky.car.home.MyCarsFragment;
import com.sky.car.util.ConfigDefinition;
import com.sky.car.widget.SHImageView;
import com.sky.widget.SHDialog;

public class CarsAdapter extends BaseAdapter implements ITaskListener{
	private Context context;
	private int type;//0:my cars  1:select brand
	private JSONArray jsonArray = new JSONArray();
	private BaseFragment fragment;
	private int position;//
	
	public void setData(JSONArray arr){
		this.jsonArray = arr;
	}
	
	public void setFragment(BaseFragment f){
		this.fragment = f;
	}
	
	public CarsAdapter(Context c,int type){
		this.context = c;
		this.type = type;
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
		private LinearLayout ll_right;
	}
	
	@Override
	public View getView(final int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_cars, null);
			holder = new ViewHolder();
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_carname);
			holder.tv_xilie = (TextView) convertView.findViewById(R.id.tv_xilie);
			holder.iv_car = (SHImageView) convertView.findViewById(R.id.iv_car); 
			holder.iv_selected = (ImageView) convertView.findViewById(R.id.iv_selected);
			holder.ll_right = (LinearLayout) convertView.findViewById(R.id.ll_right);
			if(type == 0){
				holder.ll_right.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						position = arg0;
						SHDialog.ShowProgressDiaolg(context, null);
						SHPostTaskM changeCarTask = new SHPostTaskM();
						changeCarTask.setListener(CarsAdapter.this);
						changeCarTask.setUrl(ConfigDefinition.URL+"mycarmaintanance.action");
						try {
							changeCarTask.getTaskArgs().put("carid", jsonArray.getJSONObject(arg0).getString("carid"));
							changeCarTask.getTaskArgs().put("optype", "3");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						changeCarTask.start();
					}
				});
			}
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
			holder.iv_car.setNewImgForImageSrc(true);
			try {
				if(type == 0){
					holder.iv_car.setURL(jsonArray.getJSONObject(arg0).getString("carlogo"));
					holder.tv_name.setText(jsonArray.getJSONObject(arg0).getString("carcategoryname"));
					holder.tv_xilie.setText(jsonArray.getJSONObject(arg0).getString("carseriesname"));
					if(jsonArray.getJSONObject(arg0).optInt("isactivited") == 1){
						holder.iv_selected.setVisibility(View.VISIBLE);
					}else{
						holder.iv_selected.setVisibility(View.INVISIBLE);
					}
				}else{
					holder.iv_car.setURL(jsonArray.getJSONObject(arg0).getString("carcategorylogo"));
					holder.tv_name.setText(jsonArray.getJSONObject(arg0).getString("carcategoryname"));
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return convertView;
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		((MyCarsFragment) fragment).requestMyCars();
		HomeFragment.json = jsonArray.getJSONObject(position);
	}

	@Override
	public void onTaskFailed(SHTask task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskUpdateProgress(SHTask task, int count, int total) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskTry(SHTask task) {
		// TODO Auto-generated method stub
		
	}

}
