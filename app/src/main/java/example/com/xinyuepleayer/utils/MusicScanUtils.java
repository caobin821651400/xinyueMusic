package example.com.xinyuepleayer.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.List;

import example.com.xinyuepleayer.bean.MusicInfoBean;

/**
 * 扫描歌曲工具
 * Created by caobin on 2017/1/17.
 */
public class MusicScanUtils {

    /**
     * 扫描本地歌曲
     */
    public static void scanMusic(Context context, List<MusicInfoBean> musicInfoBean) {
        //每次进来先清除list
        musicInfoBean.clear();

        /**
         *使用系统内部提供的音乐文件
         */
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            //是否为音乐
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            if (isMusic == 0) {
                continue;
            }

            //获取歌曲在系统中的id
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            //获取歌曲的歌名
            String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            //获取专辑的歌手名
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String unknown = "未知";
            artist = artist.equals("<unknown>") ? unknown : artist;
            //获取专辑名
            String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            //获取时长
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            //获取歌曲路径，如xx/xx/xx.mp3
            String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            //获取歌曲所在专辑的id
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            //专辑的uri地址
            String coverUri = getCoverUri(context, albumId);
            //全名 歌曲名-作者
            String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
            //歌曲大小
            long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            //歌曲年份
            String year = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)));

            MusicInfoBean bean = new MusicInfoBean();
            bean.setId(id);
            bean.setType(MusicInfoBean.Type.LOCAL);
            bean.setTitle(title);
            bean.setArtist(artist);
            bean.setAlbum(album);
            bean.setDuration(duration);
            bean.setUri(uri);
            bean.setCoverUri(coverUri);
            bean.setFileName(fileName);
            bean.setFileSize(fileSize);
            bean.setYear(year);
            musicInfoBean.add(bean);
        }
        cursor.close();
    }

    /**
     * 根据封面ID查找封面uri
     *
     * @param context
     * @param albumId
     * @return
     */
    private static String getCoverUri(Context context, long albumId) {
        String uri = null;
        Cursor cursor = context.getContentResolver().query(
                Uri.parse("content://media/external/audio/albums/" + albumId),
                new String[]{"album_art"}, null, null, null);
        if (cursor != null) {
            cursor.moveToNext();
            uri = cursor.getString(0);
            cursor.close();
        }
        return uri;
    }
}
