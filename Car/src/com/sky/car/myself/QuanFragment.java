package com.sky.car.myself;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.sky.base.BaseFragment;
import com.sky.car.R;
import com.sky.car.adapter.AreaAdapter.ViewHolder;
import com.sky.car.util.ConfigDefinition;
import com.sky.widget.SHDialog;

public class QuanFragment extends BaseFragment implements ITaskListener{

	private SHPostTaskM quanTask;
	private ListView mLv_quan;
	private JSONArray jsonArray;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mDetailTitlebar.setTitle("洗车券");
		mLv_quan = (ListView) view.findViewById(R.id.lv_quan);
		SHDialog.ShowProgressDiaolg(getActivity(), null);
		quanTask = new SHPostTaskM();
		quanTask.setUrl(ConfigDefinition.URL+"mywashticketsquery.action");
		quanTask.setListener(this);
		quanTask.getTaskArgs().put("tickettype", 99);
		quanTask.getTaskArgs().put("isonlyexpired", 99);
		quanTask.start();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_quan, container, false);
		return view;
	}

	class Adapter extends BaseAdapter{

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
			public TextView tv_money,tv_text,tv_start,tv_end;   
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if(convertView == null){
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_quan, null);
				holder = new ViewHolder();
				holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
				holder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
				holder.tv_start = (TextView) convertView.findViewById(R.id.tv_start);
				holder.tv_end = (TextView) convertView.findViewById(R.id.tv_end);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder)convertView.getTag();
			}
			try {
				holder.tv_money.setText(jsonArray.getJSONObject(position).optInt("washticketmoney")+"元");
				holder.tv_start.setText(jsonArray.getJSONObject(position).optString("washticketstarttime").substring(0, 10));
				holder.tv_end.setText(jsonArray.getJSONObject(position).optString("washticketendtime").substring(0, 10));
				holder.tv_text.setText("洗车只需支付"+jsonArray.getJSONObject(position).optInt("washticketmoney")+"元");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return convertView;
		}
		
	}
	
	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		JSONObject json = (JSONObject) task.getResult();
		jsonArray = json.getJSONArray("mywashtickets");
		mLv_quan.setAdapter(new Adapter());
	}

	@Override
	public void onTaskFailed(SHTask task) {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		SHDialog.showOneKeyDialog(getActivity(), task.getRespInfo().getMessage(), null);
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
