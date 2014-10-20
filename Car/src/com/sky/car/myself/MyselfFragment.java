package com.sky.car.myself;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.next.intf.ITaskListener;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.sky.base.BaseActivity;
import com.sky.base.BaseFragment;
import com.sky.base.SHContainerActivity;
import com.sky.car.R;
import com.sky.car.home.MyCarsFragment;
import com.sky.car.util.ConfigDefinition;
import com.sky.car.util.UserInfoManager;
import com.sky.car.widget.SHImageView;
import com.sky.widget.SHDialog;
import com.sky.widget.SHDialog.DialogClickListener;
import com.sky.widget.SHDialog.DialogItemClickListener;
import com.sky.widget.SHToast;

public class MyselfFragment extends BaseFragment implements ITaskListener{

	private Button mBtn_exit;
	private RelativeLayout mRl_name, mRl_call,mRl_share,mRl_about,mRl_mycar,mRl_quan;
	private TextView mTv_name,mTv_phone;
	private SHImageView mIv_photo;
	SHPostTaskM selfTask,uploadTask;
	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	public static final String IMAGE_UNSPECIFIED = "image/*";
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		ShareSDK.initSDK(getActivity());
		mDetailTitlebar.setTitle("我的信息");
		mDetailTitlebar.setLeftButton(null, null);
		mBtn_exit = (Button) view.findViewById(R.id.btn_exit);
		mRl_name = (RelativeLayout) view.findViewById(R.id.rl_name);
		mTv_name = (TextView) view.findViewById(R.id.tv_name);
		mTv_phone = (TextView) view.findViewById(R.id.tv_phone);
		mIv_photo = (SHImageView) view.findViewById(R.id.iv_photo);
		mRl_call = (RelativeLayout) view.findViewById(R.id.rl_call);
		mRl_share = (RelativeLayout) view.findViewById(R.id.rl_share);
		mRl_about = (RelativeLayout) view.findViewById(R.id.rl_about);
		mRl_mycar = (RelativeLayout) view.findViewById(R.id.rl_mycar);
		mRl_quan = (RelativeLayout) view.findViewById(R.id.rl_quan);
		OnClick onClick = new OnClick();
		mBtn_exit.setOnClickListener(onClick);
		mRl_name.setOnClickListener(onClick);
		mRl_call.setOnClickListener(onClick);
		mRl_share.setOnClickListener(onClick);
		mRl_about.setOnClickListener(onClick);
		mIv_photo.setOnClickListener(onClick);
		mRl_mycar.setOnClickListener(onClick);
		mRl_quan.setOnClickListener(onClick);
		requestSelfInfo();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_myself, container, false);
		return view;
	}
	
	private void requestSelfInfo(){
		SHDialog.ShowProgressDiaolg(getActivity(), null);
		selfTask = new SHPostTaskM();
		selfTask.setUrl(ConfigDefinition.URL+"meinfoquery.action");
		selfTask.setListener(this);
		selfTask.start();
	}

	private class OnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_exit:
				SHDialog.showDoubleKeyDialog(getActivity(), "您确定要退出账号吗？", new DialogClickListener() {

					@Override
					public void confirm() {
						// TODO Auto-generated method stub
						android.os.Process.killProcess(android.os.Process.myPid());
					}

					@Override
					public void cancel() {
						// TODO Auto-generated method stub

					}
				});
				break;
			case R.id.rl_name:
				Intent intent = new Intent(getActivity(), SHContainerActivity.class);
				intent.putExtra("class", UpdateNicknameFragment.class.getName());
				startActivityForResult(intent, 0);
				break;
			case R.id.rl_call:
				SHDialog.showDoubleKeyDialog(getActivity(), "客服工作时间：09:00-18:00\n是否现在拨打电话？", new DialogClickListener() {

					@Override
					public void confirm() {
						// TODO Auto-generated method stub
						Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+"18516063983"));
					    startActivity(intent);
					}

					@Override
					public void cancel() {
						// TODO Auto-generated method stub

					}
				});
				break;
			case R.id.rl_share:
				showShare();
				break;
			case R.id.rl_about:
				Intent intent2 = new Intent(getActivity(),SHContainerActivity.class);
				intent2.putExtra("class", AboutFragment.class.getName());
				startActivity(intent2);
				break;
			case R.id.iv_photo:
				final String[] items = new String[]{"拍照","相册"};
				SHDialog.showActionSheet(getActivity(), "选择照片方式", items, new DialogItemClickListener() {
					
					@Override
					public void onSelected(String result) {
						// TODO Auto-generated method stub
						if(result == items[0]){
							Intent it_take = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							it_take.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.png")));
							startActivityForResult(it_take, PHOTOHRAPH);
						}else{
							Intent it_local = new Intent(Intent.ACTION_GET_CONTENT, null);
							it_local.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
							startActivityForResult(it_local, PHOTOZOOM);
						}
					}
				});
				break;
			case R.id.rl_mycar:
				Intent intent_car = new Intent(getActivity(),SHContainerActivity.class);
				intent_car.putExtra("class", MyCarsFragment.class.getName());
				startActivity(intent_car);
				break;
			case R.id.rl_quan:
				Intent intent_quan = new Intent(getActivity(),SHContainerActivity.class);
				intent_quan.putExtra("class", QuanFragment.class.getName());
				startActivity(intent_quan);
				break;
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			if (data != null && data.getExtras() != null) {
				mTv_name.setText(data.getStringExtra("name"));
				SHToast.showToast(getActivity(), "保存成功！", SHToast.LENGTH_SHORT);
			}
			break;
		case PHOTOHRAPH:
			File picture = new File(Environment.getExternalStorageDirectory() + "/temp.png");
			startPhotoZoom(Uri.fromFile(picture));
			break;
		case PHOTOZOOM:
			startPhotoZoom(data.getData());
			break;
		case PHOTORESOULT:
			setResultPhoto(data.getExtras());
			break;
		}
	}

	/**
	 * 缩放裁剪图片
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 10);
		intent.putExtra("aspectY", 10);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 70);
		intent.putExtra("outputY", 70);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, PHOTORESOULT);
	}
	
	public void setResultPhoto(Bundle b) {
		if (b != null) {
			Bitmap photo = b.getParcelable("data");
			String UserPreview = bitmapToBase64(photo);
//			System.out.println("base64:"+UserPreview);
			SHDialog.ShowProgressDiaolg(getActivity(), null);
			uploadTask = new SHPostTaskM();
			uploadTask.setUrl(ConfigDefinition.URL + "meinfomodify.action");
			uploadTask.getTaskArgs().put("mynickname", mTv_name.getText().toString().trim());
			uploadTask.getTaskArgs().put("myheadicon", UserPreview);
			uploadTask.setListener(this);
			uploadTask.start();
		}
	}
	
	/**
	 * bitmap转为base64
	 * 
	 * @param bitmap
	 * @return
	 */
	public String bitmapToBase64(Bitmap bitmap) {

		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	 private void showShare() {
	        OnekeyShare oks = new OnekeyShare();
	        //关闭sso授权
	        oks.disableSSOWhenAuthorize();
	       
	        // 分享时Notification的图标和文字
	        oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
	        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
	        oks.setTitle(getString(R.string.share));
	        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
	        oks.setTitleUrl("http://sharesdk.cn");
	        // text是分享文本，所有平台都需要这个字段
	        oks.setText("content");
	        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//	        oks.setImagePath("http://www.baidu.com/img/bd_logo1.png");
	        oks.setImageUrl("http://www.baidu.com/img/bd_logo1.png");
	        // url仅在微信（包括好友和朋友圈）中使用
	        oks.setUrl("http://sharesdk.cn");
	        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
	        oks.setComment("test");
	        // site是分享此内容的网站名称，仅在QQ空间使用
	        oks.setSite(getString(R.string.app_name));
	        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//	        oks.setSiteUrl("http://sharesdk.cn");

	        // 启动分享GUI
	        oks.show(getActivity());
	   }

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		System.out.println(task.getResult().toString());
		if(task == selfTask){
			JSONObject json = (JSONObject) task.getResult();
			mTv_name.setText(json.getString("mynickname"));
			mTv_phone.setText("手机号："+UserInfoManager.getInstance().getName());
//			mIv_photo.setNewImgForImageSrc(true);
//			mIv_photo.setURL(json.getString("myheadicon"));
		}else if(task == uploadTask){
			
		}
	}

	@Override
	public void onTaskFailed(SHTask task) {  
		// TODO Auto-generated method stub
		SHDialog.dismissProgressDiaolg();
		SHDialog.showOneKeyDialog(getActivity(), task.getRespInfo().getMessage(), null);
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
