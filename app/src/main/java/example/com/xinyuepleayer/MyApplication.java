package example.com.xinyuepleayer;

import android.app.Application;

import org.xutils.x;


/**
 * Created by caobin on 2016/12/6.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(false); // 是否输出debug日志, 开启debug会影响性能.
    }
}