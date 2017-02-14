package example.com.xinyuepleayer.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.activity.MainActivity;
import example.com.xinyuepleayer.activity.MusicPlayerActivity;
import example.com.xinyuepleayer.adapter.MusicListAdapter;
import example.com.xinyuepleayer.base.BaseFragment;
import example.com.xinyuepleayer.bean.MusicInfoBean;
import example.com.xinyuepleayer.utils.MusicScanUtils;
import example.com.xinyuepleayer.utils.MyLogUtil;

/**
 * 本地音乐列表适配器
 * Created by caobin on 2017/1/11.
 */
public class MineFragment extends BaseFragment {

    private ListView mListView;
    private MusicListAdapter musicListAdapter;
    /**
     * 存放音乐信息的集合
     */
    private ArrayList<MusicInfoBean> musicInfoList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.local_fragmnet, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View v) {
        mListView = (ListView) v.findViewById(R.id.list_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    //如果正在播放，点击正在播放的歌曲没反应，点击其他可以。
                    if (getPlayService().isPlaying()) {
                        if (position == getPlayService().getPosition()) {
                            return;
                        } else {
                            getPlayService().openAudio(position);
                        }
                    } else {
                        getPlayService().openAudio(position);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 延迟加载
     */
    @Override
    protected void lazyLoad() {
        //加载本地数据，显示到listView中
        getLocalMusic();
    }

    /**
     * 获取本地音乐
     */
    private void getLocalMusic() {
        //在子线程中加载数据
        new Thread() {

            @Override
            public void run() {
                super.run();
                if (musicInfoList != null) {
                    musicInfoList = null;
                }
                musicInfoList = new ArrayList<MusicInfoBean>();
                //这里扫描歌曲,耗时操作放到子线程中
                MusicScanUtils.scanMusic(getActivity(), musicInfoList);
                //发送消息，更新UI
                handler.sendEmptyMessage(1000);
            }
        }.start();
    }

    /**
     *
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //判断是否有数据
            if (musicInfoList != null && musicInfoList.size() > 0) {
                //有数据，设置适配器，将音乐信息传递到adapter显示在列表中
                musicListAdapter = new MusicListAdapter(getActivity(), mListener);
                mListView.setAdapter(musicListAdapter);
                musicListAdapter.setList(musicInfoList);
                //提示信息隐藏
            } else {
                //没有, 提示信息显示
                toast("本地没有歌曲!");
            }
            //取消加载进度条
        }
    };

    /**
     * 6.0申请权限,
     */
    private void requestPermission() {
        // 判断当前Activity是否已经获得了该权限
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // 如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                toast("请进入设置-应用管理-打开存储权限");
            } else {
                // 进行权限请求
                ActivityCompat
                        .requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1001: {
                // 如果请求被拒绝，那么通常grantResults数组为空
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 申请成功，进行相应操作
                } else {
                    // 申请失败，可以继续向用户解释。
                    toast("没有存储读取权限,您可能无法查看保存文件");
                }
                return;
            }
        }
    }

    /**
     * 实现类，响应按钮点击事件
     */
    private MusicListAdapter.MyClickListener mListener = new MusicListAdapter.MyClickListener() {
        @Override
        public void myOnClick(int position) {
            deleteMusic(position);
        }
    };

    /**
     * 删除指定位置的音乐
     *
     * @param position
     */
    public void deleteMusic(int position) {
        //删除本地音乐
        deleteLocalMusic(position);
        //拿到本地歌曲的uri
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicInfoList.get(position).getId());
        //删除
        getActivity().getContentResolver().delete(uri, null, null);
        //删除完成从新扫描，更新列表
        getLocalMusic();
        //如果删除正在播放的音乐，播放下一首，如果只有一首歌，就停止播放

    }

    /**
     * 删除本地音乐
     *
     * @param position
     */
    private synchronized void deleteLocalMusic(int position) {
        //删除本地音乐文件
        File file = new File(musicInfoList.get(position).getUri());
        if (file.exists()) {
            file.delete();
        }
    }
}
