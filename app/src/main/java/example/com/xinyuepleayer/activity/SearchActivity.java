package example.com.xinyuepleayer.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import example.com.xinyuepleayer.IMyMusicService;
import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.adapter.SearchListAdapter;
import example.com.xinyuepleayer.base.BaseActivity;
import example.com.xinyuepleayer.bean.MusicInfoBean;
import example.com.xinyuepleayer.bean.RankMusicUrlBean;
import example.com.xinyuepleayer.bean.SearchBean;
import example.com.xinyuepleayer.request.MusicRequest;
import example.com.xinyuepleayer.service.MyMusicService;
import example.com.xinyuepleayer.utils.Constant;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 搜索歌曲界面
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener {
    private ListView mListView;
    private SearchListAdapter mAdapter;
    private EditText searchEd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        MyBindService();
    }

    private void initView() {

        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_search).setOnClickListener(this);
        searchEd = (EditText) findViewById(R.id.ed_search);
        mListView = (ListView) findViewById(R.id.list_view);
        mAdapter = new SearchListAdapter(this);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchBean.SongBean bean = (SearchBean.SongBean) mAdapter.getItem(i);
                getMusicUrl(bean.getSongid());

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_search:
                if (TextUtils.isEmpty(searchEd.getText())) {
                    return;
                }
                searchMusic();
                break;
        }
    }

    /**
     * 搜索网络歌曲
     */
    private void searchMusic() {
        String url = Constant.RANK_MUSIC_API_URL;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                //json解析
                .addConverterFactory(GsonConverterFactory.create())
                //添加RxJava
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        final MusicRequest musicRequest = retrofit.create(MusicRequest.class);
        //请求参数
        musicRequest.searchMusic(searchEd.getText().toString().trim())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SearchBean>() {
                    @Override
                    public void onCompleted() {
                        //toast("请求完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.err.println("错误信息：" + e.getMessage());
                    }

                    @Override
                    public void onNext(SearchBean searchBean) {
                        List<SearchBean.SongBean> list = new ArrayList<>();
                        list.addAll(searchBean.getSong());
                        mAdapter.setList(list);
                        closeInput();
                    }
                });
    }

    /**
     * 根据songId拿到歌曲的url地址
     *
     * @param songId
     */
    private void getMusicUrl(String songId) {
        String url = Constant.RANK_MUSIC_API_URL;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                //json解析
                .addConverterFactory(GsonConverterFactory.create())
                //添加RxJava
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        final MusicRequest musicRequest = retrofit.create(MusicRequest.class);
        //请求参数
        musicRequest.getRankMusicUrl("json", "", "webapp_music", "baidu.ting.song.playAAC", songId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RankMusicUrlBean>() {
                    @Override
                    public void onCompleted() {
                        //toast("请求完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.err.println("错误信息：" + e.getMessage());
                    }

                    @Override
                    public void onNext(RankMusicUrlBean rankMusicBean) {
                        try {
                            HashMap<Integer, MusicInfoBean> map = new HashMap<>();
                            MusicInfoBean bean = new MusicInfoBean();
                            bean.setUri(rankMusicBean.getBitrate().getShow_link());
                            bean.setTitle(rankMusicBean.getSonginfo().getTitle());
                            bean.setArtist(rankMusicBean.getSonginfo().getAuthor());
                            bean.setCoverUri(rankMusicBean.getSonginfo().getPic_radio());
                            map.put(0, bean);
                            toast("正在播放" + rankMusicBean.getSonginfo().getTitle());
                            service.openNetMusic(map);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 绑定服务
     * 这里只绑定，并没有启动，在mainActivity中已经启动过了
     */
    private void MyBindService() {
        Intent intent = new Intent(this, MyMusicService.class);
        intent.setAction("com.caobin.musicplayer.aidlService");
        bindService(intent, con, Context.BIND_AUTO_CREATE);
    }

    private IMyMusicService service;
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(con);
    }
}
