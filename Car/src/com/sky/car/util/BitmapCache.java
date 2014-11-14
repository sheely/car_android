package com.sky.car.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

/**
 * 对图片的缓存
 * 
 * @author demon
 * 
 */
public class BitmapCache {
	private static BitmapCache cache;
	/** 用于Cache内容的存储 */
	private HashMap<String, BtimapRef> bitmapRefs;
	/** 垃圾Reference的队列（所引用的对象已经被回收，则将该引用存入队列中） */
	private ReferenceQueue<Bitmap> q;

	/**
	 * 初始化操作
	 */
	private BitmapCache() {
		bitmapRefs = new HashMap<String, BitmapCache.BtimapRef>();
		q = new ReferenceQueue<Bitmap>();
	}

	/**
	 * 获取实例
	 * 
	 * @return
	 */
	public static BitmapCache getInstance() {
		if (cache == null) {
			cache = new BitmapCache();
		}
		return cache;
	}

	/**
	 * 添加图片到缓存
	 * 
	 * @param bmp
	 *            图片
	 * @param key
	 *            图片名称 [key]
	 */
	@SuppressLint("NewApi")
	public void addBitmapCache(Bitmap bmp, String key) {
		// 获取缓存名称
		String name = filename(key);
		// 清除相同的缓存
		clearCache();
		// 封装数据
		BtimapRef ref = new BtimapRef(bmp, q, name);
		bitmapRefs.put(name, ref);
	}

	/**
	 * 获取图片
	 * 
	 * @param key
	 * @return
	 */
	public Bitmap getBitmap(String url) {
		// 获取缓存名称
		String name = filename(url);
		Bitmap bmp = null;
		// 1.从缓存中查看是否有图片， 如果 有从缓存中取
		if (bitmapRefs.containsKey(name)) {
			BtimapRef bmpRef = bitmapRefs.get(name);
			bmp = bmpRef.get();
			return bmp;
		}
		return null;
	}

	/**
	 * 根据图片的路径返回名称
	 * 
	 * @param url
	 * @return
	 */
	private String filename(String url) {
		return url.substring(url.lastIndexOf("/") + 1, url.length());
	}

	/**
	 * 清除所有缓存
	 */
	public void clearAllCache() {
		clearCache();
		bitmapRefs.clear();
		System.gc();
		System.runFinalization();
	}

	/**
	 * 清除无用的缓存
	 */
	private void clearCache() {
		BtimapRef ref = null;
		while ((ref = (BtimapRef) q.poll()) != null) {
			bitmapRefs.remove(ref._key);
		}
	}

	/**
	 * 继承SoftReference，使得每一个实例都具有可识别的标识。
	 */
	private class BtimapRef extends SoftReference<Bitmap> {
		// 标识key
		private String _key = "";

		public BtimapRef(Bitmap bmp, ReferenceQueue<Bitmap> q, String key) {
			super(bmp, q);
			_key = key;
		}
	}
}
