//package com.sky.car.home;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.ImageView;
//
//import com.sky.car.R;
//
//public class PhotoActivity extends Activity {
//	
//	private static final int TAKE_PICTURE = 0;
//	private static final int CHOOSE_PICTURE = 1;
//	
//	private static final int SCALE = 5;//��Ƭ��С����
//	private ImageView iv_image = null;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_photo);
//		setTitle("Demo���ߣ�@����_Ryan");
//		
//		iv_image = (ImageView) this.findViewById(R.id.img);
//		
//		this.findViewById(R.id.btn).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				showPicturePicker(PhotoActivity.this);
//			}
//		});
//		
//		this.findViewById(R.id.btn2).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//			}
//		});
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode == RESULT_OK) {
//			switch (requestCode) {
//			case TAKE_PICTURE:
//				//�������ڱ��ص�ͼƬȡ������С����ʾ�ڽ�����
//				Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/image.jpg");
//				Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
//				//����Bitmap�ڴ�ռ�ýϴ�������Ҫ�����ڴ棬����ᱨout of memory�쳣
//				bitmap.recycle();
//				
//				//���������ͼƬ��ʾ�ڽ����ϣ������浽����
//				iv_image.setImageBitmap(newBitmap);
//				ImageTools.savePhotoToSDCard(newBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), String.valueOf(System.currentTimeMillis()));
//				break;
//
//			case CHOOSE_PICTURE:
//				ContentResolver resolver = getContentResolver();
//				//��Ƭ��ԭʼ��Դ��ַ
//				Uri originalUri = data.getData(); 
//	            try {
//	            	//ʹ��ContentProviderͨ��URI��ȡԭʼͼƬ
//					Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
//					if (photo != null) {
//						//Ϊ��ֹԭʼͼƬ�������ڴ��������������Сԭͼ��ʾ��Ȼ���ͷ�ԭʼBitmapռ�õ��ڴ�
//						Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
//						//�ͷ�ԭʼͼƬռ�õ��ڴ棬��ֹout of memory�쳣����
//						photo.recycle();
//						
//						iv_image.setImageBitmap(smallBitmap);
//					}
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}  
//				break;
//			
//			default:
//				break;
//			}
//		}
//	}
//	
//	public void showPicturePicker(Context context){
//		AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		builder.setTitle("ͼƬ��Դ");
//		builder.setNegativeButton("ȡ��", null);
//		builder.setItems(new String[]{"����","���"}, new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				switch (which) {
//				case TAKE_PICTURE:
//					Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//					Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"image.jpg"));
//					//ָ����Ƭ����·����SD������image.jpgΪһ����ʱ�ļ���ÿ�����պ����ͼƬ���ᱻ�滻
//					openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//					startActivityForResult(openCameraIntent, TAKE_PICTURE);
//					break;
//					
//				case CHOOSE_PICTURE:
//					Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
//					openAlbumIntent.setType("image/*");
//					startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
//					break;
//
//				default:
//					break;
//				}
//			}
//		});
//		builder.create().show();
//	}
//}
