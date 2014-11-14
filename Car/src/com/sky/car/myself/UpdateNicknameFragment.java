package com.sky.car.myself;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.sky.base.BaseFragment;
import com.sky.base.SHContainerActivity;
import com.sky.car.MainActivity;
import com.sky.car.R;
import com.sky.car.util.ConfigDefinition;
import com.sky.widget.SHDialog;
import com.sky.widget.SHEditText;
import com.sky.widget.SHToast;

public class UpdateNicknameFragment extends BaseFragment implements ITaskListener{

	private SHEditText mEt_nickname;
	private SHPostTaskM savaTask;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mDetailTitlebar.setTitle("修改昵称");
		mDetailTitlebar.setLeftButton(null, null);
		mDetailTitlebar.setLeftButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
			}
		});
		mDetailTitlebar.setRightButton("保存", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mEt_nickname.getText().toString().trim() == null || mEt_nickname.getText().toString().trim().length() <= 0){
					SHToast.showToast(getActivity(), "昵称不能为空", SHToast.LENGTH_SHORT);
					return;
				}
				SHDialog.ShowProgressDiaolg(getActivity(), null);
				savaTask = new SHPostTaskM();
				savaTask.setUrl(ConfigDefinition.URL+"meinfomodify.action");
				savaTask.setListener(UpdateNicknameFragment.this);
				savaTask.getTaskArgs().put("mynickname", mEt_nickname.getText().toString().trim());
				savaTask.getTaskArgs().put("myheadicon", "");
				savaTask.start();
			}
		});
		mEt_nickname = (SHEditText) view.findViewById(R.id.et_name);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_update_nickname, container, false);
		return view;
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		Intent intent = new Intent(getActivity(),MainActivity.class);
		intent.putExtra("name", mEt_nickname.getText().toString().trim());
		getActivity().setResult(Activity.RESULT_OK, intent);
		getActivity().finish();
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
