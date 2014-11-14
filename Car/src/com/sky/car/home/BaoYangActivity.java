package com.sky.car.home;

import java.io.File;
import java.io.IOException;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sky.base.BaseNormalActivity;
import com.sky.base.SHApplication;
import com.sky.car.R;
import com.sky.car.util.AudioRecorder;
import com.sky.car.util.SHLocationManager;
import com.sky.widget.SHDialog;
import com.sky.widget.SHDialog.DialogItemClickListener;
import com.sky.widget.SHToast;

public class BaoYangActivity extends BaseNormalActivity {

	private TextView mTv_location,mTv_add_photo,mTv_add_voice;
	
	private static final int MIN_RECORD_TIME = 1; // ���¼��ʱ�䣬��λ�룬0Ϊ��ʱ������
    private static final int RECORD_OFF = 0; // ����¼��
    private static final int RECORD_ON = 1; // ����¼��
    private static final String RECORD_FILENAME = "record0033"; // ¼���ļ���

//    private Button mBtnStartRecord;
//    private Button mBtnPlayRecord;
//    private TextView mTvRecordTxt;
//    private TextView mTvRecordPath;
    private TextView mTvRecordDialogTxt;
    private ImageView mIvRecVolume;

    private Dialog mRecordDialog;
    private AudioRecorder mAudioRecorder;
    private MediaPlayer mMediaPlayer;
    private Thread mRecordThread;

    private int recordState = 0; // ¼��״̬
    private float recodeTime = 0.0f; // ¼��ʱ��
    private double voiceValue = 0.0; // ¼��������ֵ
    private boolean playState = false; // ¼���Ĳ���״̬
    private boolean moveState = false; // ��ָ�Ƿ��ƶ�

    private float downY;
	
    private RelativeLayout mRl_voice;
    private TextView mTv_time,mTv_playing;
    private ImageView mIv_play;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_baoyang);
		super.onCreate(savedInstanceState);
		mDetailTitlebar.setTitle("ά�ޱ���");
		mTv_location = (TextView) findViewById(R.id.tv_location);
		mTv_location.setText(SHLocationManager.getInstance().getAddress());
		mRl_voice = (RelativeLayout) findViewById(R.id.rl_voice);
		mTv_time = (TextView) findViewById(R.id.tv_time);
		mIv_play = (ImageView) findViewById(R.id.iv_play);
		mTv_playing = (TextView) findViewById(R.id.tv_playing);
		mTv_add_photo = (TextView) findViewById(R.id.tv_photo);
		mTv_add_voice = (TextView) findViewById(R.id.tv_voice);
		OnClick onClick = new OnClick();
		mTv_add_photo.setOnClickListener(onClick);
		mIv_play.setOnClickListener(onClick);
		mTv_add_voice.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: // ���°�ť
                    if (recordState != RECORD_ON) {
                        downY = event.getY();
                        deleteOldFile();
                        mAudioRecorder = new AudioRecorder(RECORD_FILENAME);
                        recordState = RECORD_ON;
                        try {
                            mAudioRecorder.start();
                            recordTimethread();
                            showVoiceDialog(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE: // ������ָ
                    float moveY = event.getY();
                    if (downY - moveY> 50) {
                        moveState = true;
                        showVoiceDialog(1);
                    }
                    if (downY - moveY < 20) {
                        moveState = false;
                        showVoiceDialog(0);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: // �ɿ���ָ
                    if (recordState == RECORD_ON) {
                        recordState = RECORD_OFF;
                        if (mRecordDialog.isShowing()) {
                            mRecordDialog.dismiss();
                        }
                        try {
                            mAudioRecorder.stop();
                            mRecordThread.interrupt();
                            voiceValue = 0.0;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (!moveState) {
                            if (recodeTime < MIN_RECORD_TIME) {
                            	mRl_voice.setVisibility(View.GONE);
                            	mTv_add_voice.setText("��ס¼��");
                            	deleteOldFile();
                                SHToast.showToast(BaoYangActivity.this, "¼��ʱ��̫��", SHToast.LENGTH_SHORT);
                            } else {
                            	//¼�����֮��...
                            	mRl_voice.setVisibility(View.VISIBLE);
                            	mIv_play.setVisibility(View.VISIBLE);
                				mTv_playing.setVisibility(View.INVISIBLE);
                				mTv_time.setText(((int) recodeTime)+"\"");
                				mTv_add_voice.setText("����¼��");
                				new Handler().post(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										((ScrollView) findViewById(R.id.sv)).fullScroll(ScrollView.FOCUS_DOWN);
									}
								});
                            }
                        }else{
                        	deleteOldFile();
                        }
                        moveState = false;
                    }
                    break;
                }
                return false;
            }
		});
	}

	private class OnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.tv_photo:
				String[] items = new String[]{"����","���"};
				SHDialog.showActionSheet(BaoYangActivity.this, "ѡ����Ƭ��ʽ", items, new DialogItemClickListener() {
					
					@Override
					public void onSelected(String result) {
						// TODO Auto-generated method stub
						
					}
				});
				break;
			case R.id.iv_play:
                if (!playState) {
                    mMediaPlayer = new MediaPlayer();
                    try {
                        mMediaPlayer.setDataSource(getAmrPath());
                        mMediaPlayer.prepare();
                        mIv_play.setVisibility(View.INVISIBLE);
        				mTv_playing.setVisibility(View.VISIBLE);
                        playState = true;
                        mMediaPlayer.start();

                        // ���ò��Ž���ʱ����
                        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if (playState) {
                                	mIv_play.setVisibility(View.VISIBLE);
                    				mTv_playing.setVisibility(View.INVISIBLE);
                                    playState = false;
                                }
                            }
                        });
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                        playState = false;
                    } else {
                        playState = false;
                    }
                }
            
				break;
			}
		}
		
	}
	
	 // ɾ�����ļ�
    void deleteOldFile() {
    	System.out.println(Environment.getExternalStorageDirectory());
        File file = new File(Environment.getExternalStorageDirectory(),
                "/car/voiceRecord/" + RECORD_FILENAME + ".amr");
        if (file.exists()) {
        	System.out.println("exist........");
            file.getAbsoluteFile().delete();
        }
    }

    // ¼��ʱ��ʾDialog
    void showVoiceDialog(int flag) {
        if (mRecordDialog == null) {
            mRecordDialog = new Dialog(BaoYangActivity.this, R.style.DialogStyle);
            mRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mRecordDialog.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mRecordDialog.setContentView(R.layout.dialog_record);
            mIvRecVolume = (ImageView) mRecordDialog.findViewById(R.id.record_dialog_img);
            mTvRecordDialogTxt = (TextView) mRecordDialog.findViewById(R.id.record_dialog_txt);
        }
        switch (flag) {
        case 1:
            mIvRecVolume.setImageResource(R.drawable.img_talk);
            mTvRecordDialogTxt.setText("�ɿ���ָ��ȡ��¼��");
            break;

        default:
            mIvRecVolume.setImageResource(R.drawable.img_talk);
            mTvRecordDialogTxt.setText("���ϻ�����ȡ��¼��");
            break;
        }
        mTvRecordDialogTxt.setTextSize(14);
        mRecordDialog.show();
    }
 // ��ȡ�ļ��ֻ�·��
    private String getAmrPath() {
        File file = new File(Environment.getExternalStorageDirectory(),
                "/car/voiceRecord/" + RECORD_FILENAME + ".amr");
        return file.getAbsolutePath();
    }

    // ¼����ʱ�߳�
    void recordTimethread() {
        mRecordThread = new Thread(recordThread);
        mRecordThread.start();
    }
    
    // ¼���߳�
    private Runnable recordThread = new Runnable() {

        @Override
        public void run() {
            recodeTime = 0.0f;
            while (recordState == RECORD_ON) {
                // ����¼��ʱ��
                // if (recodeTime >= MAX_RECORD_TIME && MAX_RECORD_TIME != 0) {
                // imgHandle.sendEmptyMessage(0);
                // } else
                {
                    try {
                        Thread.sleep(150);
                        recodeTime += 0.15;
                        // ��ȡ����������dialog
                        if (!moveState) {
                            voiceValue = mAudioRecorder.getAmplitude();
                            System.out.println(voiceValue);
//                            recordHandler.sendEmptyMessage(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
}
