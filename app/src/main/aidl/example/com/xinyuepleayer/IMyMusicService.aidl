// IMyMusicService.aidl
package example.com.xinyuepleayer;

// Declare any non-default types here with import statements

interface IMyMusicService {


   /**
       * 根据对应的位置打开音频文件
       *
       * @param position 位置
       */
      int openAudio(int position);
       /**
        * 打开网络音乐
        *
        * @param url
        */
        void openNetMusic(String url);

      /**
       * 播放音乐
       */
       void start();

      /**
       * 暂停音乐
       */
        void pause();

      /**
       * 停止音乐
       */
       void stop();

      /**
       * 下一首
       */
       int next();

      /**
       * 上一首
       */
       int pre();

      /**
       * 得到当前进度
       */
       int getCurrentProgress();

      /**
       * 得到歌曲时长
       */
       int getDuration();

      /**
       * 得到歌曲名称
       */
       String getName();

      /**
       * 得到演唱者
       */
       String getArtist();

      /**
       * 得到歌曲路径
       */
       String getMusicPath();

      /**
       * 播放模式
       */
       void setPlayerMode(int mode);

      /**
       * 播放模式
       */
       int getPlayerMode();

        /**
         * 判断歌曲是否正在播放
        */
       boolean isPlaying();

       /**
        *改变歌曲播放进度
       */
       void goToSeek(int progress);

        /**
          * 得到封面的uri
         */
       String getImageUri();
         /**
          * 得到正在播放歌曲的位置
           */
       int getPosition();
        /**
        * 判断是否为暂停状态
       */
       boolean isPause();
       /**
        * 判断mediaPlayer是否为空
        */
        boolean mediaIsNull();
     /**
     * 得到本地歌曲数量
     */
      int getMusicCount();

       /**
       * 服务刷新歌曲列表
       */
      int refreshMusicList();
          /**
           * 删除歌曲
           * @return
           */
          int deleteMusic(int position);
}
