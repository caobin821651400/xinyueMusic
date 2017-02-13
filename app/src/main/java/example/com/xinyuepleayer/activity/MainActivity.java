package example.com.xinyuepleayer.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import example.com.xinyuepleayer.IMyMusicService;
import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.adapter.MyViewPagerAdapter;
import example.com.xinyuepleayer.base.BaseActivity;
import example.com.xinyuepleayer.bean.MusicInfoBean;
import example.com.xinyuepleayer.fragment.MineFragment;
import example.com.xinyuepleayer.fragment.OnLineFragment;
import example.com.xinyuepleayer.service.MyMusicService;
import example.com.xinyuepleayer.utils.Constant;
import example.com.xinyuepleayer.utils.MyLogUtil;
import example.com.xinyuepleayer.utils.MyUtils;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    //左侧打开侧滑按钮；搜索按钮；暂停播放按钮
    private ImageButton leftBtn, searchBtn, playerBtn;
    //我的音乐；在线音乐；地下的音乐名字；底下的演唱者；
    private TextView localTv, onLineTv, musicNameTv, musicArtistTv;
    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;//侧滑菜单
    private RelativeLayout playingLayout;//底部控制栏
    private ImageView bottomMusicImg;//底部显示歌曲专辑图片
    private SeekBar mSeekBar;//进度条
    private IMyMusicService service;//音乐播放的server
    //广播
    private MyReceiver myReceiver;
    //存放音乐信息的集合
    // private ArrayList<MusicInfoBean> musicInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        bindServiceAndStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        leftBtn = (ImageButton) findViewById(R.id.left_menu);
        searchBtn = (ImageButton) findViewById(R.id.right_search);
        playerBtn = (ImageButton) findViewById(R.id.btn_music_pause);

        localTv = (TextView) findViewById(R.id.tv_local_music);
        onLineTv = (TextView) findViewById(R.id.tv_on_line_music);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        playingLayout = (RelativeLayout) findViewById(R.id.rl_music_name_and_author);
        bottomMusicImg = (ImageView) findViewById(R.id.iv_music_image);
        mSeekBar = (SeekBar) findViewById(R.id.play_progress);
        musicNameTv = (TextView) findViewById(R.id.tv_music_name);
        musicArtistTv = (TextView) findViewById(R.id.tv_music_author);


        findViewById(R.id.ll_menu_1).setOnClickListener(this);
        findViewById(R.id.ll_menu_2).setOnClickListener(this);
        findViewById(R.id.ll_menu_3).setOnClickListener(this);
        findViewById(R.id.ll_menu_4).setOnClickListener(this);
        findViewById(R.id.rl_music_name_and_author).setOnClickListener(this);
        //下一首
        findViewById(R.id.btn_next_music).setOnClickListener(this);


        localTv.setOnClickListener(this);
        onLineTv.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        playerBtn.setOnClickListener(this);

        //viewpager适配
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MineFragment());
        adapter.addFragment(new OnLineFragment());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new MyPageChangeListener());

        changeLocalText();
        //默认第一个界面(本地音乐)
        mViewPager.setCurrentItem(0);
    }

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
     * 绑定服务并启动
     */
    private void bindServiceAndStart() {
        Intent intent = new Intent(this, MyMusicService.class);
        intent.setAction("com.caobin.musicplayer.aidlService");
        bindService(intent, con, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    @Override
    public void onClick(View view) {
        // Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_local_music:
                changeLocalText();
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_on_line_music:
                changeLineText();
                mViewPager.setCurrentItem(1);
                break;
            case R.id.left_menu:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.right_search:
                toast("搜索");
                break;
            case R.id.ll_menu_1:
                mDrawerLayout.closeDrawers();
                toast("1");
                break;
            case R.id.ll_menu_2:
                mDrawerLayout.closeDrawers();
                toast("2");
                break;
            case R.id.ll_menu_3:
                mDrawerLayout.closeDrawers();
                toast("3");
                break;
            case R.id.ll_menu_4:
                mDrawerLayout.closeDrawers();
                toast("4");
                break;
            case R.id.rl_music_name_and_author:
                launchActivity(MusicPlayerActivity.class, null);
               // overridePendingTransition(R.anim.move_in_anim, R.anim.move_out_anim);
                break;
            case R.id.btn_next_music:
                //下一首
                try {
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_music_pause: {
                //播放和暂停
                if (service != null) {
                    try {
                        if (service.isPlaying()) {
                            //此时正在播放,点击变暂停
                            service.pause();
                            playerBtn.setImageResource(R.drawable.bottom_btn_play);
                        } else {
                            //此时没有播放,点击变播放
                            service.start();
                            playerBtn.setImageResource(R.drawable.bottom_btn_pause);
                            //播放时，暂停的进度条继续跟新
                            mSeekBar.post(mRunnable);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }


    /**
     * viewpager滑动监听
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                searchBtn.setVisibility(View.INVISIBLE);
                changeLocalText();
            } else if (position == 1) {
                searchBtn.setVisibility(View.VISIBLE);
                changeLineText();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 改变本地音乐字体样式
     */
    private void changeLocalText() {
        localTv.setSelected(true);
        onLineTv.setSelected(false);
        localTv.setTextSize(19);
        onLineTv.setTextSize(15);
    }

    /**
     * 改变在线音乐字体样式
     */
    private void changeLineText() {
        onLineTv.setSelected(true);
        localTv.setSelected(false);
        onLineTv.setTextSize(19);
        localTv.setTextSize(15);
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
            try {
                //两种情况 1.第一次进入程序没有播放  2.暂停退出  3.正在播放时推出。只有第一种情况service为空
                if (service.isPlaying() || service.isPause()) {
                    //目的为了在退出程序时，service不为空，在次进来时显示正在播放的歌曲信息
                    showMusicInfo(service);
                } else {

                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /**
         * 链接失败回调
         * @param componentName
         */
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if (service != null) {
                try {
                    service.stop();
                    service = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 显示歌曲信息
     */
    private void showMusicInfo(IMyMusicService service) {
        try {
            musicNameTv.setText(service.getName());
            musicArtistTv.setText(service.getArtist());

            Glide.with(MainActivity.this).load(service.getImageUri()).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.no_music_rotate_img)
                    .crossFade()
                    .into(bottomMusicImg);

            mSeekBar.setMax(service.getDuration());
            mSeekBar.postDelayed(mRunnable, 10);
            //判断是否正在播放，显示暂停或者播放的图片
            if (service.isPlaying()) {
                playerBtn.setImageResource(R.drawable.bottom_btn_pause);
            } else {
                playerBtn.setImageResource(R.drawable.bottom_btn_play);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到server
     *
     * @return
     */
    public IMyMusicService getMusicService() {
        return service;
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
                    if (service.isPlaying()) {
                        mSeekBar.postDelayed(mRunnable, 1000);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出时解除绑定
        unbindService(con);
        //注销广播
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
            myReceiver = null;
        }
        //停止线程
        mSeekBar.removeCallbacks(mRunnable);
    }


}
