package com.sky.car.adapter;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sky.car.R;
import com.sky.widget.SHDialog;
import com.sky.widget.SHDialog.DialogItemClickListener;

public class PhotoAdapter extends BaseAdapter {
	private Activity context;
	private ArrayList<Bitmap> mlist;
	private boolean ifCanAdd = true;

	public void setData(ArrayList<Bitmap> alist) {
		this.mlist = alist;
		if(mlist.size() < 3){
			BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_add);
			mlist.add(bd.getBitmap());
			ifCanAdd = true;
		}else{
			ifCanAdd = false;
		}
	}

	public PhotoAdapter(Activity c) {
		this.context = c;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist.size();
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

	public class ViewHolder {
		public ImageView iv_item;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_photo, null);
			holder = new ViewHolder();
			holder.iv_item = (ImageView) convertView.findViewById(R.id.item_photo);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.iv_item.setImageBitmap(mlist.get(position));
		
			holder.iv_item.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(position == mlist.size()-1 && ifCanAdd){
						showSelectDialog();
					}
					
				}
			});
		return convertView;
	}

	private void showSelectDialog(){
		final String[] items = new String[]{"拍照","相册"};
		SHDialog.showActionSheet(context, "选择照片方式", items, new DialogItemClickListener() {
			
			@Override
			public void onSelected(String result) {
				// TODO Auto-generated method stub
				if(items[0].equals(result)){
					Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"car_temp.jpg"));
					//指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
					openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					context.startActivityForResult(openCameraIntent, 0);
				}else{
					Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
					openAlbumIntent.setType("image/*");
					context.startActivityForResult(openAlbumIntent, 1);
				}
			}
		});
	}
}
