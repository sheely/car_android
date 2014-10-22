package com.sky.car.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

public class ImageLoaderTask extends AsyncTask<Uri, Integer, Bitmap> {

	private ImageView iv_photo;
	private Context c;

	public ImageLoaderTask(Context c, ImageView iv) {
		super();
		this.iv_photo = iv;
		this.c = c;
	}

	@Override
	protected Bitmap doInBackground(Uri... params) {
		// TODO Auto-generated method stub
		ContentResolver resolver = c.getContentResolver();
		Bitmap photo = null;
		try {
			photo = MediaStore.Images.Media.getBitmap(resolver, params[0]);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / 8, photo.getHeight() / 8);
		// 释放原始图片占用的内存，防止out of memory异常发生
		photo.recycle();

		return smallBitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		iv_photo.setImageBitmap(result);
	}
}
