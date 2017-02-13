package example.com.xinyuepleayer.activity;

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
    //暂停/播放按钮
    private ImageView playing;

    //带旋转的imageview
    private CircleImageView rotateImage;
    //进度条
    private SeekBar mSeekBar;
    //当前播放的时间
    private TextView currentTimeTv;
    private MyUtils utils;
    private IMyMusicService service;
    private MyReceiver myReceiver;//广播


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musci_plear);
        initView();
        bindServiceAndStart();
        initData();
    }


    /**
     * 绑定服务
     */
    private void bindServiceAndStart() {
        Intent intent = new Intent(this, MyMusicService.class);
        intent.setAction("com.caobin.musicplayer.aidlService");
        bindService(intent, con, Context.BIND_AUTO_CREATE);
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
        //进度条监听
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
                //分享
                toast("分享功能以后实现!");
            }
            break;
            case R.id.playing_next:
                //下一首
                try {
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.playing_pre:
                //上一首
                try {
                    service.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    /**
     * 服务监听连接状态
     */
    private ServiceConnection con = new ServiceConnection() {
        /**
         * 链接成功回调
         * @param componentName
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = IMyMusicService.Stub.asInterface(iBinder);
            utils = new MyUtils();
            showMusicInfo(service);
            //中间旋转开始
            rotateImage.startRotation();

        }

        /**
         * 链接失败回调
         * @param componentName
         */
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if (service != null) {
                try {
                    //停止 释放
                    service.stop();
                    service = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 初始化歌曲的信息,注册广播
     */
    private void initData() {
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyMusicService.COM_CAOBIN_NOTIFY_CHANGE_MUSIC_INFO);
        registerReceiver(myReceiver, filter);
    }

    /**
     * 接受广播,更新歌曲信息
     */
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收到广播，显示歌曲信息
            //歌曲名和作者
            showMusicInfo(service);
        }
    }

    /**
     * 显示歌曲信息
     */
    private void showMusicInfo(IMyMusicService service) {
        try {
            ((TextView) findViewById(R.id.tv_music_name)).setText(service.getName());
            ((TextView) findViewById(R.id.tv_music_author)).setText(service.getArtist());
            ((TextView) findViewById(R.id.tv_music_all_time)).setText(utils.stringForTime(service.getDuration()));

            Glide.with(MusicPlayerActivity.this).load(service.getImageUri()).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //圆形
                    .transform(new CircleTransform(MusicPlayerActivity.this))
                    .error(R.drawable.no_music_rotate_img)
                    .crossFade()
                    .dontAnimate()
                    .into(rotateImage);

            //加载背景，也是加载专辑封面
            Glide.with(MusicPlayerActivity.this)
                    .load(service.getImageUri())
                    .dontAnimate()
                    .error(R.drawable.no_music_rotate_img)
                    .into(allBg);
            //透明度
            allBg.setAlpha(20);

            mSeekBar.setMax(service.getDuration());
            mSeekBar.postDelayed(mRunnable, 10);
            //判断是否正在播放，显示暂停或者播放的图片
            if (service.isPlaying()) {
                playing.setImageResource(R.drawable.play_rdi_btn_pause);
            } else {
                playing.setImageResource(R.drawable.play_rdi_btn_play);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
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
                        //每格一秒重复
                        mSeekBar.postDelayed(mRunnable, 1000);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        unbindService(con);
        //停止线程
        mSeekBar.removeCallbacks(mRunnable);
        //注销广播
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
            myReceiver = null;
        }
       // overridePendingTransition(R.anim.move_in_anim, R.anim.move_out_anim);
    }
}
