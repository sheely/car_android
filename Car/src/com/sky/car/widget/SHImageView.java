package com.sky.car.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.next.intf.ITaskListener;
import com.next.net.SHCacheType;
import com.next.net.SHGetTask;
import com.next.net.SHTask;


public class SHImageView extends ImageView implements ITaskListener {
	protected Boolean mImgForImage = false;
	protected final Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			if(!mImgForImage){
				SHImageView.this.setBackgroundDrawable((BitmapDrawable) msg.obj);
			}else{
				SHImageView.this.setImageDrawable((BitmapDrawable) msg.obj);
			}
		}
	};
	public void setNewImgForImageSrc( Boolean flag){
		mImgForImage = flag;
	}
	public void setDefaultImageResource( int id){
		this.setBackgroundResource(id);
	}
	public void setDefaultImageSrc( int id){
		this.setImageResource(id);
	}
	
	public SHImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SHImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SHImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void setURL(String url) {

		SHGetTask post = new SHGetTask();
		post.setChacheType(SHCacheType.PERSISTENT);
		post.setIsMainThread(false);
		post.setUrl(url);
		post.setListener(this);
		post.start();
	}
	
	public void onTaskFinished(SHTask task) {
		if (task.getResult() != null) {
			Bitmap mp = bytes2Bimap((byte[]) task.getResult());
			if (mp != null) {
				Message msg = new Message();
				msg.what = 0;
				msg.obj = new BitmapDrawable(null, mp);
				mHandler.sendMessage(msg);
			}
		}
	}

	private Bitmap bytes2Bimap(byte[] b){  
        if(b.length!=0){  
            return BitmapFactory.decodeByteArray(b, 0, b.length);  
        }  
        else {  
            return null;  
        }  
  } 
	public void onTaskFailed(SHTask task) {

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
