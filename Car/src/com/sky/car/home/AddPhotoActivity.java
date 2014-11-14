package com.sky.car.home;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.next.util.SHEnvironment;
import com.sky.base.BaseNormalActivity;
import com.sky.base.DetailTitlebar;
import com.sky.car.R;
import com.sky.car.adapter.PhotoAdapter;
import com.sky.car.util.ImageTools;
import com.sky.car.util.PhotoTransManager;

public class AddPhotoActivity extends BaseNormalActivity {

	private static final int TAKE_PICTURE = 0;
	private static final int CHOOSE_PICTURE = 1;

	private static final int SCALE = 5;// 照片缩小比例
	private ImageView iv_image = null;
	private DetailTitlebar titlebar;
	private GridView gv_photo;
	private PhotoAdapter adapter;
	private ArrayList<Bitmap> list = new ArrayList<Bitmap>();
	private Button btn_confirm;
	private boolean isFull = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_add_photo);
		titlebar = (DetailTitlebar) findViewById(R.id.detailTitlebar);
		titlebar.setTitle("选择照片");
		System.out.println("add。。。onCreate");
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AddPhotoActivity.this,WashMapActivity.class);
				if(!isFull){
					list.remove(list.size()-1);
				}
				PhotoTransManager.getInstance().setList(list);
				finish();
			}
		});
		gv_photo = (GridView) findViewById(R.id.gv_photo);
		adapter = new PhotoAdapter(this);
		if(PhotoTransManager.getInstance().getList() != null && PhotoTransManager.getInstance().getList().size() > 0){
			if(PhotoTransManager.getInstance().getList().size() == 3){
				isFull = true;
			}
			list = PhotoTransManager.getInstance().getList();
		}
		adapter.setData(list);
		gv_photo.setAdapter(adapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case TAKE_PICTURE:
				//将保存在本地的图片取出并缩小后显示在界面上
				Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/car_temp.jpg");
				Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / 6, bitmap.getHeight() / 6);
				//由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
				bitmap.recycle();
				list.remove(list.size()-1);
				list.add(newBitmap);
				if(list.size() == 3){
					isFull = true;
				}else{
					isFull = false;
				}
				adapter.setData(list);
				adapter.notifyDataSetChanged();
				//将处理过的图片显示在界面上，并保存到本地
//				iv_image.setImageBitmap(newBitmap);
//				ImageTools.savePhotoToSDCard(newBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), String.valueOf(System.currentTimeMillis()));
				break;

			case CHOOSE_PICTURE:
				ContentResolver resolver = getContentResolver();
				//照片的原始资源地址
				Uri originalUri = data.getData(); 
	            try {
	            	//使用ContentProvider通过URI获取原始图片
					Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
					if (photo != null) {
						//为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
						Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / 6, photo.getHeight() / 6);
						//释放原始图片占用的内存，防止out of memory异常发生
						photo.recycle();
						list.remove(list.size()-1);
						list.add(smallBitmap);
						if(list.size() == 3){
							isFull = true;
						}else{
							isFull = false;
						}
						adapter.setData(list);
						adapter.notifyDataSetChanged();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}  
				break;
			
			default:
				break;
			}
		}
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("username", SHEnvironment.getInstance().getLoginID());
		outState.putString("password", SHEnvironment.getInstance().getPassword());
		outState.putParcelableArrayList("list",list);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		SHEnvironment.getInstance().setLoginId(savedInstanceState.getString("username"));
		SHEnvironment.getInstance().setPassword(savedInstanceState.getString("password"));
		list = savedInstanceState.getParcelableArrayList("list");
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if(!isFull){
			list.remove(list.size()-1);
		}
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	
}
