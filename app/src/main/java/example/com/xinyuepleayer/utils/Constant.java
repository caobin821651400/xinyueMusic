package example.com.xinyuepleayer.utils;

/**
 * 保存各种常量
 * Created by caobin on 2016/12/13.
 */
public class Constant {
    /**天气API地址**/
    public static final String WEATHER_API_URL="http://wthrcdn.etouch.cn/weather_mini";
    /** 新闻API**/
    public static final String NEWS_API_URL="http://v.juhe.cn/toutiao/index";
   /**NBA数据API**/
   public static final String NBA_API_URL="http://op.juhe.cn/onebox/basketball/nba";
    /**热歌榜API**/
    public static final String RANK_MUSIC_API_URL ="http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&callback=&" +
            "from=webapp_music&method=baidu.ting.billboard.billList";
    /**根据songId拿到歌曲的url地址**/
    public static final String RANK_MUSIC_API_URL_SONGID ="http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&callback=&" +
            "from=webapp_music&method=baidu.ting.song.playAAC";
    /**
     *
     */
    public static final  String SEARCG_MUSIC_API="http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&calback=&" +
            "from=webapp_music&method=baidu.ting.search.catalogSug";
   /**保存正在播放音乐的位置**/
   public static final String SAVE_CURRENT_MUSIC_POSITION = "save_current_music_position";


}
