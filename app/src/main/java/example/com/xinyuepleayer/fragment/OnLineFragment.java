package example.com.xinyuepleayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;

import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.base.BaseFragment;
import example.com.xinyuepleayer.utils.GlideImageLoader;

/**
 * 网络歌曲界面
 * Created by caobin on 2017/1/11.
 */
public class OnLineFragment extends BaseFragment {

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
    public void onStart() {
        super.onStart();
        mBanner.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mBanner.stopAutoPlay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBanner = null;
    }
}
