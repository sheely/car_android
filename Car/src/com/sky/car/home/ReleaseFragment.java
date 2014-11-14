package com.sky.car.home;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sky.car.R;
import com.sky.car.adapter.OptionAdapter;
import com.sky.car.util.PhotoTransManager;

public class ReleaseFragment extends Fragment {
	
	private static ReleaseFragment instance = null;
	private ImageView iv_add_photo;
	private TextView tv_time,tv_num;
	private RelativeLayout rl_point;
	private MediaPlayer mMediaPlayer;
	private boolean playState = false; // 录音的播放状态
	private OptionAdapter adapter;
	private GridView mGv;
	private EditText mEt_content;

	// 单例模式
	public static ReleaseFragment getInstance() {
		if (instance == null) {
			instance = new ReleaseFragment();
		}
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.dialog_map, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		iv_add_photo = (ImageView) view.findViewById(R.id.iv_add_image);
		tv_time = (TextView) view.findViewById(R.id.tv_time);
		tv_time.setText(((int) ((WashMapActivity)getActivity()).recodeTime)+"\"");
		rl_point = (RelativeLayout) view.findViewById(R.id.rl_point);
		tv_num = (TextView) view.findViewById(R.id.tv_num);
		mGv = (GridView) view.findViewById(R.id.gv_option);
		mEt_content = (EditText) view.findViewById(R.id.et_content);
		RelativeLayout rl_play = (RelativeLayout) view.findViewById(R.id.rl_play);
		adapter = new OptionAdapter(getActivity());
		mGv.setAdapter(adapter);
		if(getArguments() != null){
			rl_play.setVisibility(View.GONE);
			mEt_content.setVisibility(View.VISIBLE);
			mEt_content.setText(getArguments().getString("content"));
		}
		rl_play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!playState){
					mMediaPlayer = new MediaPlayer();
//					mMediaPlayer = MediaPlayer.create(getActivity(), 1);
					try {
//						mMediaPlayer.reset();
						mMediaPlayer.setDataSource(getAmrPath());
						mMediaPlayer.prepare();
						playState = true;
						mMediaPlayer.start();

						// 设置播放结束时监听
						mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

							@Override
							public void onCompletion(MediaPlayer mp) {
								if (playState) {
									playState = false;
								}
							}
						});
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		iv_add_photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent it_local = new Intent(Intent.ACTION_GET_CONTENT, null);
//				it_local.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//				getActivity().startActivityForResult(it_local, 0);
				Intent intent = new Intent(getActivity(),AddPhotoActivity.class);
				startActivity(intent);
			}
		});
	}

	// 获取文件手机路径
		private String getAmrPath() {
			File file = new File(Environment.getExternalStorageDirectory(), "/car/voiceRecord/" + "record0033" + ".amr");
			return file.getAbsolutePath();
		}
	
	public void setImage(){
		ArrayList<Bitmap> list = PhotoTransManager.getInstance().getList();
		if(list != null){
			if(list.size() != 0){
				rl_point.setVisibility(View.VISIBLE);
				tv_num.setText(""+list.size());
			}
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setImage();
	}
	
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
////		super.onActivityResult(requestCode, resultCode, data);
//		if(data != null){
//			switch(requestCode){
//			case 0:
//				ContentResolver resolver = getActivity().getContentResolver();
//				//照片的原始资源地址
//				Uri originalUri = data.getData(); 
////				new ImageLoaderTask(this,iv_photo1).execute(originalUri);
//	            try {
//	            	//使用ContentProvider通过URI获取原始图片
//					Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
//					
//					if (photo != null) {
//						//为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
//						Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / 5, photo.getHeight() / 5);
//						//释放原始图片占用的内存，防止out of memory异常发生
//						photo.recycle();
//						rl_photo1.setVisibility(View.VISIBLE);
//						iv_photo1.setImageBitmap(smallBitmap);
//					}
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}  
////				startPhotoZoom(data.getData());
//				break;
//			}
//		}
//	}

}
