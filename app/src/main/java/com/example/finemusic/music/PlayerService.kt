package com.example.finemusic.music

import android.os.Handler
import android.os.Looper
import com.example.finemusic.models.MusicInfo
import com.example.finemusic.utils.log
import com.example.finemusic.utils.msg


/**
 * 音频播放服务
 */
class PlayerService {

    /**
     * 当前播放的歌曲
     */
    private var curPlayingIndex = 0

    /**
     * 播放列表
     */
    private val playList: MutableList<Pair<MusicInfo, Int>> = ArrayList()

    /**
     * 播放器
     */
    private val mediaPlayer = android.media.MediaPlayer().apply {
        setOnCompletionListener {
            nextMusic()
        }
    }

    /**
     * 设置定时刷新的Handler
     */
    private val handler = Handler(Looper.myLooper()!!)

    private val getCurPlayPositionRunnable = Runnable() {
        getCurPlayPosition()
    }

    companion object {
        private var playerService: PlayerService? = null

        fun getInstance(): PlayerService {
            if (playerService == null) playerService = PlayerService()
            return playerService!!
        }
    }

    /**
     * 获取当前播放器播放的位置
     */
    private fun getCurPlayPosition() {
        if (mediaPlayer.isPlaying) {
            //当前播放的市场
            val currentPosition = mediaPlayer.currentPosition
            //整个媒体的总时长
            val duration = mediaPlayer.duration

            val percent = (currentPosition * 100 / duration)

            "currentPosition: $currentPosition, duration: $duration".log()

            if (onMusicPlayListener != null) onMusicPlayListener!!.onPlayPositionChanged(
                currentPosition,
                duration,
                percent
            )
        }

        handler.postDelayed(getCurPlayPositionRunnable, 1000)
    }

    /**
     * 获取当前正在播放的歌曲信息
     */
    private fun getCurMusicInfo(): Pair<MusicInfo, Int>? {
        try {
            if (playList.size == 0) {
                "Sorry not found any music in play list".msg()
                return null
            }

            if (curPlayingIndex >= playList.size) {
                "Sorry the music player inline error. Please restart the app.".msg()
                return null
            }

            return playList[curPlayingIndex]
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
    }

    /**
     * 加载歌曲地址
     */
    private fun loadMusicPlaySource(): String {
        //TODO: mock network source
        val musicInfo = getCurMusicInfo() ?: return ""

        if (onMusicPlayListener != null) onMusicPlayListener!!.onPlaySourceChange(musicInfo.first)

        return "http://10.0.2.2:5120/music/${musicInfo.first.fileSrc}"
    }

    /**
     * 设置进度监视器 和 返回开始状态
     */
    private fun startPlayStatusListener() {
        handler.postDelayed(getCurPlayPositionRunnable, 0)
        if (onMusicPlayListener != null) onMusicPlayListener!!.onPlayStatusChanged(true)

    }

    /**
     * 清除进度监视器 和 返回结束状态
     */
    private fun clearPlayStatusListener() {
        handler.removeCallbacksAndMessages(null)
        if (onMusicPlayListener != null) onMusicPlayListener!!.onPlayStatusChanged(false)
    }

    /**
     * 处理当前音频的播放状态
     */
    fun switchPlayStatus() {
        if (mediaPlayer.isPlaying) pause()
        else start()
    }

    /**
     * 设置播放列表
     */
    fun setPlayList(lsList: MutableList<Pair<MusicInfo, Int>>) {
        playList.clear()
        curPlayingIndex = 0
        playList.addAll(lsList)
    }

    /**
     * 设置播放音乐
     */
    fun playMusic() {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
            mediaPlayer.setDataSource(loadMusicPlaySource())
            mediaPlayer.prepare()
            mediaPlayer.start()
            startPlayStatusListener()
        } catch (ex: Exception) {
            "Sorry play music failed! Please try again later".msg()
        }
    }

    /**
     * 开始播放
     */
    fun start() {
        try {
            if (!mediaPlayer.isPlaying)
                mediaPlayer.start()
            startPlayStatusListener()
        } catch (ex: Exception) {
            "Sorry play music failed! Please try again later".msg()
        }
    }

    /**
     * 暂停播放
     */
    fun pause() {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                clearPlayStatusListener()
            }

        } catch (ex: Exception) {
            "Sorry pause music failed! Please try again later".msg()
        }
    }

    /**
     * 下一首歌曲
     */
    fun nextMusic() {
        if (curPlayingIndex >= playList.size - 1) return
        curPlayingIndex += 1
        playMusic()
    }

    /**
     * 设置进度
     */
    fun setMusicProgress(percent: Int) {
        try {
            val eachValue = mediaPlayer.duration / 100
            mediaPlayer.seekTo(percent * eachValue)
            start()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    //#region 播放器时间监听
    interface OnMusicPlayListener {
        fun onPlayPositionChanged(curPosition: Int, duration: Int, percent: Int)
        fun onPlayStatusChanged(isPlaying: Boolean)
        fun onPlaySourceChange(musicInfo: MusicInfo)
    }

    private var onMusicPlayListener: OnMusicPlayListener? = null

    fun setOnMusicPlayListener(listener: OnMusicPlayListener) {
        onMusicPlayListener = listener
    }
    //#endregion
}