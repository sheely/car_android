package com.sky.car.util;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class PhotoTransManager {

	private static PhotoTransManager instance;

	public static PhotoTransManager getInstance() {
		if (instance == null) {
			instance = new PhotoTransManager();
		}
		return instance;
	}

	private ArrayList<Bitmap> list_temp;

	public ArrayList<Bitmap> getList() {
		return list_temp;
	}

	public void setList(ArrayList<Bitmap> tlist) {
		this.list_temp = tlist;
	}
	
	public void clear(){
		if(list_temp != null){
			list_temp.clear();
		}
	}
}
