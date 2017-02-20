package example.com.xinyuepleayer.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.adapter.RankRecycleAdapter;
import example.com.xinyuepleayer.base.BaseActivity;
import example.com.xinyuepleayer.bean.RankMusicBean;
import example.com.xinyuepleayer.bean.RankMusicBean.SongListBean;
import example.com.xinyuepleayer.request.RankRequest;
import example.com.xinyuepleayer.utils.Constant;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 排行榜列表
 */
public class RankingActivity extends BaseActivity implements View.OnClickListener {

    private int musicType;//歌曲类型
    private String title;//标题
    private RecyclerView mRecyclerView;
    private ImageView topBg;//顶部的背景图
    private List<SongListBean> rankList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        if (getIntent() != null) {
            title = getIntent().getStringExtra("title");
            musicType = getIntent().getIntExtra("type", 2);
        }
        initView();
        getData(musicType);
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
        mRecyclerView.addItemDecoration(new DividerItemDecoration(RankingActivity.this, LinearLayoutManager.VERTICAL));
    }

    /**
     * 获取网络数据
     *
     * @param musicType
     */
    private void getData(int musicType) {
        //base URL
        String url = Constant.RANK_MUSIC_API_URL;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                //json解析
                .addConverterFactory(GsonConverterFactory.create())
                //添加RxJava
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        final RankRequest rankRequest = retrofit.create(RankRequest.class);
        //请求参数 "成都"
        rankRequest.getRankMusicList("json", "", "webapp_music", "baidu.ting.billboard.billList", musicType, 10, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RankMusicBean>() {
                    @Override
                    public void onCompleted() {
                        //在这里可以dismissDialog
                        toast("请求完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.err.println("错误信息：" + e.getMessage());
                    }

                    @Override
                    public void onNext(RankMusicBean rankMusicBean) {
                        rankList = new ArrayList<SongListBean>();
                        rankList.addAll(rankMusicBean.getSong_list());
                        mRecyclerView.setAdapter(new RankRecycleAdapter(RankingActivity.this, rankList));
                    }
                });
    }

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
}
