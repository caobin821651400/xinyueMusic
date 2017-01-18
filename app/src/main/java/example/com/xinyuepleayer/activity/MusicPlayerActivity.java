package example.com.xinyuepleayer.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import example.com.xinyuepleayer.IMyMusicService;
import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.base.BaseActivity;
import example.com.xinyuepleayer.service.MyMusicService;
import example.com.xinyuepleayer.view.CircleImageView;
import example.com.xinyuepleayer.view.CircleTransform;

public class MusicPlayerActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 歌曲在列表中的位置
     */
    private int position;
    //歌曲名
    private String name;
    //演唱者
    private String artist;
    //封面URI
    private String imageUri;
    //播放界面背景
    private ImageView allBg;
    //各种按钮
    private ImageView playing, next, pre;

    private IMyMusicService service;
    //带旋转的imageview
    private CircleImageView rotateImage;
    /**
     * 监听废物连接状态
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
            if (service != null) {
                try {
                    service.openAudio(position);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musci_plear);
        //拿到列表中歌曲的位置
        if (getIntent() != null) {
            position = getIntent().getIntExtra("position", 0);
            name = getIntent().getStringExtra("name");
            artist = getIntent().getStringExtra("artist");
            imageUri = getIntent().getStringExtra("imageUri");
        }
        initView();
        bindServiceAndStart();
    }

    private void initView() {
        rotateImage = (CircleImageView) findViewById(R.id.rotate_circle_image_view);
        allBg = (ImageView) findViewById(R.id.iv_music_player_all_bg);
        playing = (ImageView) findViewById(R.id.playing_play);
        playing.setOnClickListener(this);

        //中间旋转图片
        rotateImage.startRotation();
        //歌曲名和作者
        ((TextView) findViewById(R.id.tv_music_name)).setText(name);
        ((TextView) findViewById(R.id.tv_music_author)).setText(artist);
        //加载专辑封面
        Glide.with(this).load(imageUri).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new CircleTransform(this))
                .error(R.drawable.no_music_rotate_img)
                .crossFade()
                .into(rotateImage);
        //加载背景，也是加载专辑封面
        Glide.with(this)
                .load(imageUri)
                .error(R.drawable.no_music_rotate_img)
                .into(allBg);
        //透明度
        allBg.setAlpha(20);
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
        switch (view.getId()) {
            case R.id.playing_play:
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
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出时解除绑定
        unbindService(con);
    }
}
