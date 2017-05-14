package example.com.xinyuepleayer.activity;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import example.com.xinyuepleayer.IMyMusicService;
import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.adapter.RankRecycleAdapter;
import example.com.xinyuepleayer.base.BaseActivity;
import example.com.xinyuepleayer.bean.MusicInfoBean;
import example.com.xinyuepleayer.bean.RankMusicBean;
import example.com.xinyuepleayer.bean.RankMusicBean.SongListBean;
import example.com.xinyuepleayer.bean.RankMusicUrlBean;
import example.com.xinyuepleayer.service.MyMusicService;
import example.com.xinyuepleayer.utils.Constant;
import example.com.xinyuepleayer.utils.MyLogUtil;

/**
 * 排行榜列表
 */
public class RankingActivity extends BaseActivity implements View.OnClickListener,
        RankRecycleAdapter.onRecyclerViewItemClickListener, RankRecycleAdapter.moreItemClickListener {

    private int musicType;//歌曲类型
    private String title;//标题
    private RecyclerView mRecyclerView;
    private ImageView topBg;//顶部的背景图
    private List<SongListBean> rankList;
    private RankRecycleAdapter mAdapter;
    private BroadcastReceiver broadcastReceiver;//下载完成的广播

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        if (getIntent() != null) {
            title = getIntent().getStringExtra("title");
            musicType = getIntent().getIntExtra("type", 2);
        }
        initView();
        MyBindService();
        getMusicListData(musicType);
    }


    private void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText(title);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        topBg = (ImageView) findViewById(R.id.iv_rank_top_bg);
        setTopBg();
        findViewById(R.id.btn_back).setOnClickListener(this);


        //设置recyclerView
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        //分割线
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(RankingActivity.this, LinearLayoutManager.VERTICAL));
        //监听事件
        mAdapter = new RankRecycleAdapter(RankingActivity.this, this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 获取歌曲列表
     *
     * @param musicType
     */
    private void getMusicListData(int musicType) {
        //base URL
        String url = Constant.RANK_MUSIC_API_URL;
        final RequestParams params = new RequestParams(url);
        params.addBodyParameter("type", musicType + "");//歌曲类型
        params.addBodyParameter("size", "50");//一页请求多少条数据
        params.addBodyParameter("offset", "0");//从第几页开始请求
        showDLG();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //请求成功之后
                rankList = new ArrayList<SongListBean>();
                rankList.addAll(new Gson().fromJson(result, RankMusicBean.class).getSong_list());
                mAdapter.setList(rankList);
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //如果请求失败，打印失败日志
                MyLogUtil.d("错误 ：" + ex.getMessage());
            }
            @Override
            public void onCancelled(CancelledException cex) {

            }
            @Override
            public void onFinished() {
                //关闭进度框
                disMissDLG();
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

    /**
     * 设置顶部的背景
     */
    private void setTopBg() {
        if (musicType == 2) {
            topBg.setImageResource(R.drawable.ic_new_music);
        } else if (musicType == 25) {
            topBg.setImageResource(R.drawable.ic_net_music);
        } else if (musicType == 21) {
            topBg.setImageResource(R.drawable.ic_om_music);
        } else if (musicType == 14) {
            topBg.setImageResource(R.drawable.ic_movie_music);
        } else if (musicType == 20) {
            topBg.setImageResource(R.drawable.ic_hy_music);
        } else {
            topBg.setImageResource(R.drawable.ic_jd_music);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * recyclerView点击事件,播放音乐
     *
     * @param songId
     */
    @Override
    public void onItemClick(String songId) {
        getMusicUrl(songId, true);
    }


    /**
     * "更多"点击回调
     *
     * @param songId
     */
    @Override
    public void moreClickListener(final String songId) {
        //通过songId拿到音乐的url地址
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("确认要下载这首歌曲吗?");
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                getMusicUrl(songId, false);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    /**
     * 根据songId拿到歌曲的url地址
     *
     * @param songId
     */
    private void getMusicUrl(String songId, final boolean isCanPlaying) {

        String url = Constant.RANK_MUSIC_API_URL_SONGID;
        final RequestParams params = new RequestParams(url);
        //参数，songId
        params.addBodyParameter("songid", songId);
        showDLG();
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                MyLogUtil.d("结果 ：" + result);
                RankMusicUrlBean rankMusicBean = new Gson().fromJson(result, RankMusicUrlBean.class);
                if (isCanPlaying) {
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
                } else {
                    //请求完成下载歌曲
                    downLoadMusic(rankMusicBean.getBitrate().getShow_link(), rankMusicBean.getSonginfo().getTitle());
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                MyLogUtil.d("错误 ：" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                disMissDLG();
            }
        });
    }

    /**
     * 下载音乐
     *
     * @param url 音乐的url地址
     * @param name 音乐名称
     */
    private void downLoadMusic(String url, String name) {
        toast("正在下载：" + name);
        DownloadManager downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //设置状态栏中显示Notification,下载完成不消失
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //设置可用的网络类型，wifi下可以进行下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //不显示下载界面
        request.setVisibleInDownloadsUi(false);
        //放到外部存储中的公共音乐文件夹的方法：
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, name);
        //将请求加入请求队列 downLoadManager会自动调用对应的服务执行者个请求
        long id = downloadManager.enqueue(request);
        downLoadComplete();
    }

    /**
     * 是否下载完成
     */
    private void downLoadComplete() {
        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                toast("下载完成");
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(con);
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }
}
