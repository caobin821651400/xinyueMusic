package example.com.xinyuepleayer.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import example.com.xinyuepleayer.IMyMusicService;
import example.com.xinyuepleayer.activity.MainActivity;
import example.com.xinyuepleayer.utils.MyLogUtil;

/**
 * Created by caobin on 2016/12/6.
 */
public abstract class BaseFragment extends Fragment {


    private IMyMusicService mMusicPlayService;
    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // MyLogUtil.d("onCreate");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //MyLogUtil.d("onViewCreated");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //MyLogUtil.d("setUserVisibleHint");
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 供子类调用 获取service
     * 现在是从activity直接获取service 不自己绑定service
     *
     * @return
     */
    protected IMyMusicService getPlayService() {
        mMusicPlayService = ((MainActivity) getActivity()).getMusicService();

        if (mMusicPlayService == null) {
            Log.e("iiiiiii", "mMusicPlayService == null");
        }
        return mMusicPlayService;
    }

    /**
     * 可见
     */
    protected void onVisible() {
        lazyLoad();
    }


    /**
     * 不可见
     */
    protected void onInvisible() {


    }


    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    protected abstract void lazyLoad();


    /**
     * toast任何类型的数据
     *
     * @param object
     */
    public void toast(String object) {
        if (TextUtils.isEmpty(object)) {
            return;
        }
        Toast.makeText(getActivity(), object, Toast.LENGTH_SHORT).show();
    }

    /**
     * 跳转类
     *
     * @param cls
     * @param bundle
     */
    protected void launchActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(getActivity(), cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
}
