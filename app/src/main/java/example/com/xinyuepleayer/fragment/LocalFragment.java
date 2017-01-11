package example.com.xinyuepleayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.base.BaseFragment;

/**
 * Created by caobin on 2017/1/11.
 */
public class LocalFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        initView();
    }

    private void initView() {

    }

    /**
     * 延迟加载
     */
    @Override
    protected void lazyLoad() {
        System.err.println("haha"+"local");
    }
}
