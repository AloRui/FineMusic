package com.example.finemusic.music

import android.os.Handler
import com.example.finemusic.models.MusicInfo
import com.example.finemusic.storage.Downloader
import com.example.finemusic.storage.FileStorage
import com.example.finemusic.storage.Shared.setInt
import com.example.finemusic.utils.MyApp
import com.example.finemusic.utils.log
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream

/**
 * 管理当前的歌曲列表
 */
object MusicManager {

    // 播放模式：单曲循环
    const val SINGLE_PLAY_MODE = 0

    // 播放模式：列表循环
    const val LOOP_PLAY_MODE = 1

    // 播放模式：随机播放
    const val RANDOM_PLAY_MODE = 2

    //播放模式为网络播放
    const val SOURCE_TYPE_NET = 0

    //播放模式为本地播放
    const val SOURCE_TYPE_LOCAL = 1

    // 当前播放模式
    private var currentPlayMode = 1

    // 当前播放的歌曲
    private var currentMusic: MusicInfo? = null

    // 播放列表
    private val musicList: MutableList<MusicInfo> = ArrayList()

    private val handler = Handler()

    //播放器
    private var player: Player = Player.getInstance().apply {
        setOnCompletionListener {
            startPlayMusic(autoPlay())
        }
    }

    private var onPlayModeChangeListener: ((Int) -> Unit)? = null

    fun setOnPlayModeChangeListener(listener: (Int) -> Unit) {
        this.onPlayModeChangeListener = listener
    }

    /**
     * 追加音乐到播放列表
     */
    fun addMusic2List(musicInfo: MusicInfo) {
        if (musicList.any { a -> a.id == musicInfo.id })
            musicList.removeAll(musicList.filter { a -> a.id == musicInfo.id }.toMutableList())
        musicList.add(musicInfo)
    }

    /**
     * 追加音乐到播放列表
     */
    fun insertMusic2List(musicInfo: MusicInfo, index: Int) {
        if (musicList.any { a -> a.id == musicInfo.id })
            musicList.removeAll(musicList.filter { a -> a.id == musicInfo.id }.toMutableList())
        musicList.add(index, musicInfo)
    }

    fun replaceMusicList(musicList: MutableList<MusicInfo>) {
        this.musicList.clear()
        this.musicList.addAll(musicList)
    }

    /**
     * 清理播放列表
     */
    fun clearMusicList() {
        musicList.clear()
        killPlayer()
    }

    /**
     * 设置播放模式
     */
    fun setPlayerMode(mode: Int) {
        "play_mode".setInt(mode)
        currentPlayMode = mode

        if (onPlayModeChangeListener != null) {
            onPlayModeChangeListener!!.invoke(mode)
        }
    }

    /**
     * 开始播放
     */
    fun playMusic(musicInfo: MusicInfo) {

        if (currentMusic == null) {
            MyApp.fineMusicNotification.openNotification()
        }

        startPlayMusic(musicInfo)

        listenerList.map {
            it.onPlayStatusChanged(true)
        }

        startMediaPlayPositionListener()
    }

    /**
     * 暂停播放
     */
    fun pausePlay() {
        player.pause()
        listenerList.map {
            it.onPlayStatusChanged(false)
        }

        removeMediaPlayPositionListener()
    }

    /**
     * 下一首歌曲
     */
    fun nextMusic() {
        val newMusicInfo = getNextMusic() ?: return
        startPlayMusic(newMusicInfo)
    }

    /**
     * 上一首歌曲
     */
    fun prevMusic() {
        val newMusicInfo = getPrevMusic() ?: return
        startPlayMusic(newMusicInfo)
    }

    /**
     * 对当前播放的歌曲进行健康检查
     */
    fun serviceHealthCheck(): Boolean {
        return MyApp.getFineMusicService()!!.serviceHealthCheck()
    }

    /**
     * 获取当前是否在播放歌曲
     */
    fun isPlaying(): Boolean {
        return player.isPlaying()
    }

    /**
     * 设置当前播放的进度
     */
    fun seekTo(position: Int) {
        val player = player

        val pos = position * (player.getCurrentPlayDuration()!! / 100)
        player.seekTo(pos)
    }

    /**
     * 获取当前播放的歌曲
     */
    fun getCurrentMusic(): MusicInfo? {
        return currentMusic
    }

    /**
     * 杀死播放器
     */
    private fun killPlayer() {
        player.clearCurrentMedia()
    }

    /**
     * 自动播放
     */
    private fun autoPlay(): MusicInfo? {
        var newMusicInfo: MusicInfo? = null

        when (currentPlayMode) {
            SINGLE_PLAY_MODE -> {
                if (currentMusic == null)
                    newMusicInfo = musicList.first()
                newMusicInfo = currentMusic
            }

            LOOP_PLAY_MODE -> {
                newMusicInfo = getNextMusic()
            }

            RANDOM_PLAY_MODE -> {
                newMusicInfo = getRandomMusic()
            }
        }

        if (newMusicInfo == null)
            return null

        return newMusicInfo
    }

    /**
     * 获取列表中下一首歌曲
     */
    private fun getNextMusic(): MusicInfo? {
        if (musicList.isEmpty())
            return null
        if (currentMusic == null)
            return musicList.first()

        val curIndex = musicList.indexOf(currentMusic)

        if (curIndex >= musicList.size - 1)
            return musicList.first()

        return musicList[curIndex + 1]
    }

    /**
     * 获取列表中上一首歌曲
     */
    private fun getPrevMusic(): MusicInfo? {
        if (musicList.isEmpty())
            return null
        if (currentMusic == null)
            return musicList.first()

        val curIndex = musicList.indexOf(currentMusic)

        if (curIndex <= 0)
            return musicList.last()

        return musicList[curIndex - 1]
    }

    /**
     * 获取随机歌曲
     */
    private fun getRandomMusic(): MusicInfo? {
        if (musicList.isEmpty()) {
            return null
        }



        return musicList[(0 until musicList.size).random().log()]
    }

    /**
     * 播放音乐
     */
    private fun startPlayMusic(musicInfo: MusicInfo?) {
        try {
            if (currentMusic == null || currentMusic?.id != musicInfo?.id || (currentMusic!!.id == musicInfo!!.id && player.getCurrentPlayPosition() >= player.getCurrentPlayDuration())) {
                listenerList.map {
                    it.onPlaySourceChange(musicInfo!!)
                }

                currentMusic = musicInfo

                val source = getMusicSource(currentMusic!!)
                source?.apply {
                    if (first == SOURCE_TYPE_NET) {
                        player.setNetworkSource(source.second.log() as String)
                    } else {
                        player.setLocalSource(source.second.log() as FileDescriptor)
                    }
                }
            }

            player.play()
        } catch (e: Exception) {
            e.log()
        }
    }

    /**
     * 获取当前播放的歌曲的资源
     */
    private fun getMusicSource(musicInfo: MusicInfo): Pair<Int, Any>? {
        try {
            musicInfo.apply {
                val fileName = FileStorage.findFileNameFromFileId(this.id)
                if (fileName.isEmpty()) {
                    Downloader.downloadMusicFile(this.fileSrc) {
                        FileStorage.saveMusicFileToLocation(this.id, it)
                    }
                    "load network source".log()
                    return Pair(SOURCE_TYPE_NET, "http://10.0.2.2:5120/music/${this.fileSrc}".log())
                }
                val localSource = getFileURIFromFileName(fileName)
                return Pair(SOURCE_TYPE_LOCAL, localSource)
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
    }

    /**
     * 获取文件的URI
     */
    private fun getFileURIFromFileName(fileName: String): FileDescriptor {
        val file = File(MyApp.ctx.filesDir, fileName)
        val fileInputStream = FileInputStream(file)
        return fileInputStream.fd
    }

    /**
     * 开始播放器时间监听
     */
    private fun startMediaPlayPositionListener() {
        handler.postDelayed({
            getCurrentPlayPosition()
        }, 0)
    }

    /**
     * 移除播放器时间监听
     */
    private fun removeMediaPlayPositionListener() {
        handler.removeCallbacksAndMessages(null)
    }

    /**
     * 获取当前播放的位置
     */
    private fun getCurrentPlayPosition() {
        val position = player.getCurrentPlayPosition()
        val duration = player.getCurrentPlayDuration()

        listenerList.map {
            it.onPlayPositionChanged(
                position,
                duration,
                if (position == 0 || duration == 0) 0 else position * 100 / duration
            )
        }

        handler.postDelayed({ getCurrentPlayPosition() }, 1000)
    }

    //#region 播放器时间监听
    interface OnMusicPlayListener {
        fun onPlayPositionChanged(curPosition: Int, duration: Int, percent: Int)
        fun onPlayStatusChanged(isPlaying: Boolean)
        fun onPlaySourceChange(musicInfo: MusicInfo)
    }

    private val listenerList = mutableListOf<OnMusicPlayListener>()

    fun setOnMusicPlayListener(listener: OnMusicPlayListener) {
        listenerList.add(listener)
    }
    //#endregion
}