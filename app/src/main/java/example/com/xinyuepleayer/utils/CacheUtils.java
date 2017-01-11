package example.com.xinyuepleayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 缓存数据
 * Created by caobin on 2016/11/22.
 */
public class CacheUtils {

    /**
     * 得到布尔类型值
     *
     * @param context
     * @param key
     * @return
     */
    public static Boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("caobin_news", Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    /**
     * 保存布尔类型值
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences("caobin_news", Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 缓存文本数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("caobin_news", Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    /**
     * 获取缓存数据
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("caobin_news", Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }
}
