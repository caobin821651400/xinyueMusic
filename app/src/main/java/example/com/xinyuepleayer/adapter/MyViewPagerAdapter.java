package example.com.xinyuepleayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页viewpager适配器
 * Created by caobin on 2017/1/11.
 */
public class MyViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;

    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList = new ArrayList<>();
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        if (mFragmentList == null) {
            return 0;
        }
        return mFragmentList.size();
    }
}
