package com.sky.car.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

/**
 * 瀵瑰浘鐗囩殑缂撳瓨
 * 
 * @author demon
 * 
 */
public class BitmapCache {
	private static BitmapCache cache;
	/** 鐢ㄤ簬Cache鍐呭鐨勫瓨鍌� */
	private HashMap<String, BtimapRef> bitmapRefs;
	/** 鍨冨溇Reference鐨勯槦鍒楋紙鎵�寮曠敤鐨勫璞″凡缁忚鍥炴敹锛屽垯灏嗚寮曠敤瀛樺叆闃熷垪涓級 */
	private ReferenceQueue<Bitmap> q;

	/**
	 * 鍒濆鍖栨搷浣�
	 */
	private BitmapCache() {
		bitmapRefs = new HashMap<String, BitmapCache.BtimapRef>();
		q = new ReferenceQueue<Bitmap>();
	}

	/**
	 * 鑾峰彇瀹炰緥
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
	 * 娣诲姞鍥剧墖鍒扮紦瀛�
	 * 
	 * @param bmp
	 *            鍥剧墖
	 * @param key
	 *            鍥剧墖鍚嶇О [key]
	 */
	@SuppressLint("NewApi")
	public void addBitmapCache(Bitmap bmp, String key) {
		// 鑾峰彇缂撳瓨鍚嶇О
		String name = filename(key);
		// 娓呴櫎鐩稿悓鐨勭紦瀛�
		clearCache();
		// 灏佽鏁版嵁
		BtimapRef ref = new BtimapRef(bmp, q, name);
		bitmapRefs.put(name, ref);
	}

	/**
	 * 鑾峰彇鍥剧墖
	 * 
	 * @param key
	 * @return
	 */
	public Bitmap getBitmap(String url) {
		// 鑾峰彇缂撳瓨鍚嶇О
		String name = filename(url);
		Bitmap bmp = null;
		// 1.浠庣紦瀛樹腑鏌ョ湅鏄惁鏈夊浘鐗囷紝 濡傛灉 鏈変粠缂撳瓨涓彇
		if (bitmapRefs.containsKey(name)) {
			BtimapRef bmpRef = bitmapRefs.get(name);
			bmp = bmpRef.get();
			return bmp;
		}
		return null;
	}

	/**
	 * 鏍规嵁鍥剧墖鐨勮矾寰勮繑鍥炲悕绉�
	 * 
	 * @param url
	 * @return
	 */
	private String filename(String url) {
		return url.substring(url.lastIndexOf("/") + 1, url.length());
	}

	/**
	 * 娓呴櫎鎵�鏈夌紦瀛�
	 */
	public void clearAllCache() {
		clearCache();
		bitmapRefs.clear();
		System.gc();
		System.runFinalization();
	}

	/**
	 * 娓呴櫎鏃犵敤鐨勭紦瀛�
	 */
	private void clearCache() {
		BtimapRef ref = null;
		while ((ref = (BtimapRef) q.poll()) != null) {
			bitmapRefs.remove(ref._key);
		}
	}

	/**
	 * 缁ф壙SoftReference锛屼娇寰楁瘡涓�涓疄渚嬮兘鍏锋湁鍙瘑鍒殑鏍囪瘑銆�
	 */
	private class BtimapRef extends SoftReference<Bitmap> {
		// 鏍囪瘑key
		private String _key = "";

		public BtimapRef(Bitmap bmp, ReferenceQueue<Bitmap> q, String key) {
			super(bmp, q);
			_key = key;
		}
	}
}
