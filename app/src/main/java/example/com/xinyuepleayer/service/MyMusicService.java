package example.com.xinyuepleayer.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import example.com.xinyuepleayer.IMyMusicService;
import example.com.xinyuepleayer.bean.MusicInfoBean;


/**
 * 音乐播放服务
 * Created by caobin on 2017/1/16.
 */
public class MyMusicService extends Service {
    //歌曲信息封装类对象
    private MusicInfoBean musicInfo;
    // 存放歌曲的集合
    private ArrayList<MusicInfoBean> musicInfoList;
    //歌曲的位置
    private int position;
    //播放器
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        getMusicData();
    }

    /**
     * 得到音乐列表
     */
    private void getMusicData() {
        new Thread() {

            @Override
            public void run() {
                super.run();
                musicInfoList = new ArrayList<>();
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objects = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//名称
                        MediaStore.Audio.Media.DURATION,//时长
                        MediaStore.Audio.Media.SIZE,//大小
                        MediaStore.Audio.Media.DATA,//数据的绝对地址
                        MediaStore.Audio.Media.ARTIST,//艺术家，作者

                };
                Cursor cursor = resolver.query(uri, objects, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        //对应数组取相应的值
                        MusicInfoBean bean = new MusicInfoBean();

                        String name = cursor.getString(0);
                        bean.setName(name);

                        long duration = cursor.getLong(1);
                        bean.setDuration(duration);

                        long size = cursor.getLong(2);
                        bean.setSize(size);

                        String data = cursor.getString(3);
                        bean.setUrl(data);

                        String artist = cursor.getString(4);
                        bean.setArtist(artist);

                        musicInfoList.add(bean);
                    }
                    //关闭
                    cursor.close();
                }
            }
        }.start();
    }

    private IMyMusicService.Stub stub = new IMyMusicService.Stub() {
        MyMusicService service = MyMusicService.this;

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public int getCurrentProgress() throws RemoteException {
            return service.getCurrentProgress();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getName() throws RemoteException {
            return service.getName();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getMusicPath() throws RemoteException {
            return service.getMusicPath();
        }

        @Override
        public void setPlayerMode(int mode) throws RemoteException {
            service.setPlayerMode(mode);
        }

        @Override
        public int getPlayerMode() throws RemoteException {
            return service.getPlayerMode();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    /**
     * 根据对应的位置打开音频文件
     *
     * @param position 位置
     */
    private void openAudio(int position) {
        this.position = position;
        if (musicInfoList != null && musicInfoList.size() > 0) {
            musicInfo = musicInfoList.get(position);
            //如果不为空释放在new
            if (mediaPlayer != null) {
                mediaPlayer.release();
               // mediaPlayer.reset();
            }

            try {
                mediaPlayer = new MediaPlayer();
                //准备完成监听
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                //播放完监听
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                //播放失败
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                //拿到路径
                mediaPlayer.setDataSource(musicInfo.getUrl());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(this, "没有音乐", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 播放音乐
     */
    public void start() {
        mediaPlayer.start();
    }

    /**
     * 暂停音乐
     */
    public void pause() {
        mediaPlayer.pause();
    }

    /**
     * 停止音乐
     */
    public void stop() {
        mediaPlayer.stop();
    }

    /**
     * 下一首
     */
    public void next() {
    }

    /**
     * 上一首
     */
    public void pre() {
    }

    /**
     * 得到当前进度
     */
    public int getCurrentProgress() {
        return 0;
    }

    /**
     * 得到歌曲时长
     */
    public int getDuration() {
        return 0;
    }

    /**
     * 得到歌曲名称
     */
    public String getName() {
        return "";
    }

    /**
     * 得到演唱者
     */
    public String getArtist() {
        return "";
    }

    /**
     * 得到歌曲路径
     */
    public String getMusicPath() {
        return "";
    }

    /**
     * 播放模式
     */
    public void setPlayerMode(int mode) {
    }

    /**
     * 播放模式
     */
    public int getPlayerMode() {
        return 0;
    }

    /**
     * 准备完成
     */
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            start();
        }
    }

    /**
     * 播放完成
     */
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            next();
        }
    }

    /**
     * 播放失败监听
     */
    class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            next();
            return true;
        }
    }


}
