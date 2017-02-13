package example.com.xinyuepleayer.activity;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import example.com.xinyuepleayer.IMyMusicService;
import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.base.BaseActivity;
import example.com.xinyuepleayer.service.MyMusicService;
import example.com.xinyuepleayer.utils.MyUtils;
import example.com.xinyuepleayer.view.CircleImageView;
import example.com.xinyuepleayer.view.CircleTransform;

public class MusicPlayerActivity extends BaseActivity implements View.OnClickListener {
    //播放界面背景
    private ImageView allBg;
    //各种按钮
    private ImageView playing, next, pre;

    private IMyMusicService service;
    //带旋转的imageview
    private CircleImageView rotateImage;
    //进度条
    private SeekBar mSeekBar;
    //当前播放的时间
    private TextView currentTimeTv;
    private MyUtils utils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musci_plear);
        initView();
        initListener();
    }


    private void initView() {
        rotateImage = (CircleImageView) findViewById(R.id.rotate_circle_image_view);
        allBg = (ImageView) findViewById(R.id.iv_music_player_all_bg);
        playing = (ImageView) findViewById(R.id.playing_play);
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        currentTimeTv = (TextView) findViewById(R.id.tv_music_already_time);
        findViewById(R.id.ib_music_player_back).setOnClickListener(this);//返回
        findViewById(R.id.ib_music_player_share).setOnClickListener(this);//分享
        findViewById(R.id.playing_next).setOnClickListener(this);//下一首
        findViewById(R.id.playing_pre).setOnClickListener(this);//上一首

        playing.setOnClickListener(this);

        //中间旋转图片
        rotateImage.startRotation();
    }

    /**
     * 各种监听事件
     */
    private void initListener() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (b) {
                    try {
                        service.goToSeek(progress);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }




    /**
     * 点击事件处理
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.playing_play: {
                //播放和暂停
                if (service != null) {
                    try {
                        if (service.isPlaying()) {
                            //此时正在播放,点击变暂停
                            service.pause();
                            playing.setImageResource(R.drawable.play_rdi_btn_play);
                            rotateImage.pauseRotation();
                        } else {
                            //此时没有播放,点击变播放
                            service.start();
                            playing.setImageResource(R.drawable.play_rdi_btn_pause);
                            rotateImage.resumeRotation();
                            //播放时，暂停的进度条继续跟新
                            mSeekBar.post(mRunnable);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case R.id.ib_music_player_back: {
                //返回
                finish();
            }
            break;
            case R.id.ib_music_player_share: {
                toast("分享功能以后实现!");
            }
            break;
            case R.id.playing_next:
                try {
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.playing_pre:
                try {
                    service.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    /**
     * 进度条更新线程
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mSeekBar != null) {
                try {
                    //1. 得到当前进度
                    int currentTime = service.getCurrentProgress();
                    //2.设置进度
                    mSeekBar.setProgress(currentTime);
                    //3.更新进度
                    currentTimeTv.setText(utils.stringForTime(currentTime));
                    if (service.isPlaying()) {
                        mSeekBar.postDelayed(mRunnable, 1000);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };




//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //退出时解除绑定
//        unbindService(con);
//        //注销广播
//        if (myReceiver != null) {
//            unregisterReceiver(myReceiver);
//            myReceiver = null;
//        }
//        //停止线程
//        mSeekBar.removeCallbacks(mRunnable);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //停止线程
        mSeekBar.removeCallbacks(mRunnable);
    }
}
