package example.com.xinyuepleayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import example.com.xinyuepleayer.IMyMusicService;
import example.com.xinyuepleayer.bean.MusicInfoBean;
import example.com.xinyuepleayer.utils.MusicScanUtils;


/**
 * 音乐播放服务
 * Created by caobin on 2017/1/16.
 */
public class MyMusicService extends Service {
    public static final String COM_CAOBIN_NOTIFY_CHANGE_MUSIC_INFO = "com.caobin.notify.change.music.info";
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
                //这里扫描歌曲,耗时操作放到子线程中
                MusicScanUtils.scanMusic(getBaseContext(), musicInfoList);
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

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        @Override
        public void goToSeek(int progress) throws RemoteException {
            service.goToSeek(progress);
        }

        @Override
        public String getImageUri() throws RemoteException {
            return service.getImageUri();
        }

        @Override
        public int getPosition() throws RemoteException {
            return service.getPosition();
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
//                mediaPlayer.release();
                mediaPlayer.reset();
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
                mediaPlayer.setDataSource(musicInfo.getUri());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "没有音乐", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     */
    public int getPosition() {
        return position;
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
        position++;
        //下一首
        if (musicInfoList != null) {
            openAudio(position % (musicInfoList.size()));
        }
    }

    /**
     * 上一首
     */
    public void pre() {
        if (position == 0) {
            Toast.makeText(MyMusicService.this, "已经到头了!", Toast.LENGTH_SHORT).show();
        } else if (position > 0) {
            position = position - 1;
            openAudio(position);
        } else {

        }
    }

    /**
     * 得到当前进度
     */
    public int getCurrentProgress() {
        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 得到歌曲总时长
     */
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    /**
     * 得到歌曲名称
     */
    public String getName() {
        return musicInfo.getTitle();
    }

    /**
     * 得到演唱者
     */
    public String getArtist() {
        return musicInfo.getArtist();
    }

    /**
     * 得到封面的uri
     */
    public String getImageUri() {
        return musicInfo.getCoverUri();
    }

    /**
     * 得到歌曲路径
     */
    public String getMusicPath() {
        return musicInfo.getUri();
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
     * 判断是否正在播放
     */
    private boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        } else {
            return false;
        }
    }

    /**
     * 改变歌曲播放进度
     */
    private void goToSeek(int progress) {
        mediaPlayer.seekTo(progress);
    }

    /**
     * 准备完成
     */
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            //通知activity改变音乐信息
            notifyChange(COM_CAOBIN_NOTIFY_CHANGE_MUSIC_INFO);
            start();
        }
    }

    /**
     * 准备完成 发送广播通知activity改变音乐信息
     *
     * @param action
     */
    private void notifyChange(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
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
