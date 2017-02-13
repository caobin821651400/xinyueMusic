package example.com.xinyuepleayer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.provider.SyncStateContract;

import java.util.Formatter;
import java.util.Locale;

/**
 * 工具类
 * Created by caobin on 2017/1/18.
 */
public class MyUtils {
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public MyUtils() {
        // 转换成字符串的时间
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

    }

    /**
     * 保存正在播放的歌曲位置
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveCurrentMusicPosition(Context context, final String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SAVE_CURRENT_MUSIC_POSITION,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 得到正在播放的歌曲位置
     *
     * @param context
     * @param key
     */
    public static int getCurrentMusicPosition(Context context, final String key) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SAVE_CURRENT_MUSIC_POSITION,
                Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    /**
     * 把毫秒转换成：1:20:30这种形式
     *
     * @param timeMs
     * @return
     */
    public String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;

        int minutes = (totalSeconds / 60) % 60;

        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 判断是否是网络的资源
     *
     * @param uri
     * @return
     */
    public boolean isNetUri(String uri) {
        boolean reault = false;
        if (uri != null) {
            if (uri.toLowerCase().startsWith("http") || uri.toLowerCase().startsWith("rtsp") || uri.toLowerCase().startsWith("mms")) {
                reault = true;
            }
        }
        return reault;
    }


    /**
     * 得到网络速度
     * 每隔两秒调用一次
     *
     * @param context
     * @return
     */
    public String getNetSpeed(Context context) {
        String netSpeed = "0 kb/s";
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB;
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        netSpeed = String.valueOf(speed) + " kb/s";
        return netSpeed;
    }
}
