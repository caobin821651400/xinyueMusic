package example.com.xinyuepleayer.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by caobin on 2016/12/6.
 */
public abstract class BaseFragment extends Fragment {

    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
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
