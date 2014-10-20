package com.sky.car.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.sky.base.BaseFragment;
import com.sky.base.SHContainerActivity;
import com.sky.car.R;
import com.sky.car.adapter.AreaAdapter;

public class SelectAreaFragment extends BaseFragment {

	private GridView gv_area;
	AreaAdapter adapter;
	private String[] items;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mDetailTitlebar.setTitle("×ÖÄ¸Ñ¡Ôñ");
		gv_area = (GridView) view.findViewById(R.id.gv_pro);
		gv_area.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), SHContainerActivity.class);
				intent.putExtra("class", AddCarFragment.class.getName());
				intent.putExtra("area", items[position]);
				getActivity().setResult(0, intent);
				getActivity().finish();
			}
		});
		items = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		adapter = new AreaAdapter(getActivity(), items);
		gv_area.setAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_select_pro, container, false);
		return view;
	}

}
