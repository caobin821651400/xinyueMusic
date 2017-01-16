package example.com.xinyuepleayer.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.activity.MusicPlayerActivity;
import example.com.xinyuepleayer.adapter.MusicListAdapter;
import example.com.xinyuepleayer.base.BaseFragment;
import example.com.xinyuepleayer.bean.MusicInfoBean;

/**
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
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                launchActivity(MusicPlayerActivity.class, bundle);
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
                musicInfoList = new ArrayList<>();
                ContentResolver resolver = getContext().getContentResolver();
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
                musicListAdapter = new MusicListAdapter(getActivity());
                mListView.setAdapter(musicListAdapter);
                musicListAdapter.setList(musicInfoList);

                //提示信息隐藏
            } else {
                //没有
                //提示信息显示
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
}
