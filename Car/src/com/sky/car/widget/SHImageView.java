package com.sky.car.widget;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.next.intf.ITaskListener;
import com.next.net.SHCacheType;
import com.next.net.SHGetTask;
import com.next.net.SHTask;
import com.sky.car.util.BitmapCache;

@SuppressLint("HandlerLeak")
public class SHImageView extends ImageView implements ITaskListener {

	private Bitmap bitmap = null;// 图片的bitmap对象
	private String imageUrl = null;// 图片的路径
	protected Boolean mImgForImage = true;
	// 屏幕的宽高
	private float width = 480f, height = 800f;

	protected final Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			if (!mImgForImage) {
				SHImageView.this
						.setBackgroundDrawable((BitmapDrawable) msg.obj);
			} else {
				SHImageView.this.setImageDrawable((BitmapDrawable) msg.obj);
			}
		}
	};

	public void setNewImgForImageSrc(Boolean flag) {
		mImgForImage = flag;
	}

	public void setDefaultImageResource(int id) {
		this.setBackgroundResource(id);
	}

	public void setDefaultImageSrc(int id) {
		this.setImageResource(id);
	}

	public SHImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SHImageView(Context context) {
		super(context);
	}

	public SHImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setURL(String url) {
		this.imageUrl = url;
		// 去缓存中查找是否存在此对象
		bitmap = BitmapCache.getInstance().getBitmap(imageUrl);
		if (bitmap == null) {
			Log.e("SHImageView", "从网络中获取图片");
			SHGetTask post = new SHGetTask();
			post.setChacheType(SHCacheType.PERSISTENT);
			post.setIsMainThread(false);
			post.setUrl(url);
			post.setListener(this);
			post.start();
		} else {
			bitmap = comp(bitmap);
			Log.e("SHImageView", "从缓存中获取图片");
			Message msg = new Message();
			msg.what = 0;
			msg.obj = new BitmapDrawable(null, bitmap);
			mHandler.sendMessage(msg);
		}
	}

	public void onTaskFinished(SHTask task) {
		if (task.getResult() != null) {
			bitmap = bytes2Bimap((byte[]) task.getResult());
			if (bitmap != null) {
				bitmap = comp(bitmap);
				// 存放到缓存中
				BitmapCache.getInstance().addBitmapCache(bitmap, imageUrl);
				Message msg = new Message();
				msg.what = 0;
				msg.obj = new BitmapDrawable(null, bitmap);
				mHandler.sendMessage(msg);
			} else {
				Log.i("SHImageView", "从网络获取图片失败");
			}
		} else {
			Log.i("SHImageView", "task.getResult()==null!!!!!");
		}
	}

	private Bitmap bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			try {
				return BitmapFactory.decodeByteArray(b, 0, b.length);
			} catch (OutOfMemoryError e) {
				Log.e("SHImageView", e.getMessage());
				BitmapCache.getInstance().clearAllCache();
				return null;
			}
		} else {
			return null;
		}
	}

	public void onTaskFailed(SHTask task) {

	}

	@Override
	public void onTaskUpdateProgress(SHTask task, int count, int total) {

	}

	@Override
	public void onTaskTry(SHTask task) {

	}

	private Bitmap comp(Bitmap image) {
		Bitmap bitmap = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		try {
			bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		} catch (OutOfMemoryError e) {
			Log.e("SHImageView", e.getMessage());
			BitmapCache.getInstance().clearAllCache();
		}
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = this.height;// 这里设置高度为800f
		float ww = this.width;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		try {
			bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		} catch (OutOfMemoryError e) {
			Log.e("SHImageView", e.getMessage());
			BitmapCache.getInstance().clearAllCache();
		}
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	private static Bitmap compressImage(Bitmap image) {
		Bitmap bitmap = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
		while (baos.toByteArray().length / 1024 > 100) {
			// 重置baos即清空baos
			baos.reset();
			// 这里压缩options%，把压缩后的数据存放到baos中
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			// 每次都减少10
			options -= 10;
		}
		// 把压缩后的数据baos存放到ByteArrayInputStream中
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		// 把ByteArrayInputStream数据生成图片
		try {
			bitmap = BitmapFactory.decodeStream(isBm, null, null);
		} catch (OutOfMemoryError e) {
			Log.e("SHImageView", e.getMessage());
			BitmapCache.getInstance().clearAllCache();
		}
		return bitmap;
	}

	/**
	 * 清除bitmap对象
	 */
	public void clearBitmap() {

	}

	/**
	 * 设置图片的宽高
	 */
	public void setWidthAndHeigth(float w, float h) {
		if (w > 0) {
			this.width = w;
		}
		if (h > 0) {
			this.height = h;
		}
	}

}
