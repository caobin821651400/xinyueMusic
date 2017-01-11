package example.com.xinyuepleayer;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import example.com.xinyuepleayer.adapter.MyViewPagerAdapter;
import example.com.xinyuepleayer.base.BaseActivity;
import example.com.xinyuepleayer.fragment.LocalFragment;
import example.com.xinyuepleayer.fragment.OnLineFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton leftBtn, rightBtn;
    private TextView localTv, onLineTv;
    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        leftBtn = (ImageButton) findViewById(R.id.left_menu);
        rightBtn = (ImageButton) findViewById(R.id.right_search);
        localTv = (TextView) findViewById(R.id.tv_local_music);
        onLineTv = (TextView) findViewById(R.id.tv_on_line_music);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        findViewById(R.id.ll_menu_1).setOnClickListener(this);
        findViewById(R.id.ll_menu_2).setOnClickListener(this);
        findViewById(R.id.ll_menu_3).setOnClickListener(this);
        findViewById(R.id.ll_menu_4).setOnClickListener(this);

        localTv.setOnClickListener(this);
        onLineTv.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        //viewpager适配
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LocalFragment());
        adapter.addFragment(new OnLineFragment());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new MyPageChangeListener());

        changeLocalText();
        mViewPager.setCurrentItem(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_local_music:
                changeLocalText();
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_on_line_music:
                changeLineText();
                mViewPager.setCurrentItem(1);
                break;
            case R.id.left_menu:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.right_search:
                toast("搜索");
                break;
            case R.id.ll_menu_1:
                mDrawerLayout.closeDrawers();
                toast("1");
                break;
            case R.id.ll_menu_2:
                mDrawerLayout.closeDrawers();
                toast("2");
                break;
            case R.id.ll_menu_3:
                mDrawerLayout.closeDrawers();
                toast("3");
                break;
            case R.id.ll_menu_4:
                mDrawerLayout.closeDrawers();
                toast("4");
                break;
        }
    }

    /**
     * viewpager滑动监听
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                changeLocalText();
            } else if (position == 1) {
                changeLineText();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 改变本地音乐字体样式
     */
    private void changeLocalText() {
        localTv.setSelected(true);
        onLineTv.setSelected(false);
        localTv.setTextSize(19);
        onLineTv.setTextSize(17);
    }

    /**
     * 改变在线音乐字体样式
     */
    private void changeLineText() {
        onLineTv.setSelected(true);
        localTv.setSelected(false);
        onLineTv.setTextSize(19);
        localTv.setTextSize(17);
    }


    private long waitTime = 2000;
    private long touchTime = 0;

    /**
     * 再按一次退出程序
     * 判断在一定的时间内连续点击两次才退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - touchTime) >= waitTime) {
                toast("再按一次，退出程序!");
                touchTime = currentTime;
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
