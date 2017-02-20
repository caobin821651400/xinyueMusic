package example.com.xinyuepleayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;

import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.activity.RankingActivity;
import example.com.xinyuepleayer.base.BaseFragment;
import example.com.xinyuepleayer.utils.GlideImageLoader;

/**
 * 网络歌曲界面
 * Created by caobin on 2017/1/11.
 */
public class OnLineFragment extends BaseFragment implements View.OnClickListener {

    private Banner mBanner;
    //存放轮播图的集合
    private List<Integer> bannerList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.on_line_fragmnet, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mBanner = (Banner) view.findViewById(R.id.image_banner);
        view.findViewById(R.id.iv_new_music).setOnClickListener(this);
        view.findViewById(R.id.iv_net_music).setOnClickListener(this);
        view.findViewById(R.id.iv_om_music).setOnClickListener(this);
        view.findViewById(R.id.iv_movie_music).setOnClickListener(this);
        view.findViewById(R.id.iv_hy_music).setOnClickListener(this);
        view.findViewById(R.id.iv_old_music).setOnClickListener(this);
    }

    @Override
    protected void lazyLoad() {
        bannerList = new ArrayList<>();
        bannerList.add(R.drawable.img_banner_one);
        bannerList.add(R.drawable.img_banner_two);
        bannerList.add(R.drawable.img_banner_three);
        bannerList.add(R.drawable.img_banner_four);
        bannerList.add(R.drawable.img_banner_five);

        //设置图片加载器
        setBanner();
        //开始轮播
        mBanner.start();
    }

    /**
     * 设置图片加载器
     * 这里用的是github上的，https://github.com/youth5201314/banner
     * 需要的自己去看，样式不一
     */
    private void setBanner() {
        mBanner.setImageLoader(new GlideImageLoader());
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片集合
        mBanner.setImages(bannerList);
        //点击事件
        mBanner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                toast("我是第" + position + "个");
            }
        });
    }


    @Override
    public void onClick(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.iv_new_music:
                bundle = new Bundle();
                bundle.putInt("type", 2);
                bundle.putString("title", "新歌榜");
                launchActivity(RankingActivity.class, bundle);
                break;
            case R.id.iv_net_music:
                bundle = new Bundle();
                bundle.putInt("type", 25);
                bundle.putString("title", "网络歌曲");
                launchActivity(RankingActivity.class, bundle);
                break;
            case R.id.iv_om_music:
                bundle = new Bundle();
                bundle.putInt("type", 21);
                bundle.putString("title", "欧美金曲");
                launchActivity(RankingActivity.class, bundle);
                break;
            case R.id.iv_movie_music:
                bundle = new Bundle();
                bundle.putInt("type", 14);
                bundle.putString("title", "影视金曲");
                launchActivity(RankingActivity.class, bundle);
                break;
            case R.id.iv_hy_music:
                bundle = new Bundle();
                bundle.putInt("type", 20);
                bundle.putString("title", "华语金曲");
                launchActivity(RankingActivity.class, bundle);
                break;
            case R.id.iv_old_music:
                bundle = new Bundle();
                bundle.putInt("type", 22);
                bundle.putString("title", "经典老歌");
                launchActivity(RankingActivity.class, bundle);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mBanner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        mBanner.stopAutoPlay();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBanner.startAutoPlay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBanner = null;
    }

}
