package example.com.xinyuepleayer.request;



import example.com.xinyuepleayer.bean.RankMusicBean;
import example.com.xinyuepleayer.bean.RankMusicUrlBean;
import example.com.xinyuepleayer.bean.SearchBean;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by caobin on 2017/2/18.
 */
public interface MusicRequest {
    @GET("ting")
    Observable<RankMusicBean> getRankMusicList(@Query("format") String format, @Query("callback") String callback, @Query("from") String from,
                                               @Query("method") String method, @Query("type") int type, @Query("size") int size, @Query("offset") int offset);

    @GET("ting")
    Observable<RankMusicUrlBean> getRankMusicUrl(@Query("format") String format, @Query("callback") String callback, @Query("from") String from,
                                                 @Query("method") String method, @Query("songid") String songid);

    @GET("ting?format=json&calback=&from=webapp_music&method=baidu.ting.search.catalogSug")
    Observable<SearchBean> searchMusic(@Query("query") String query);
}
