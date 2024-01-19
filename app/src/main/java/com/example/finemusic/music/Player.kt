package com.example.finemusic.music

import android.media.MediaPlayer
import com.example.finemusic.utils.log
import java.io.FileDescriptor

class Player {

    private lateinit var thenPlayerFinishedCallback: () -> Unit

    companion object {
        private var player: Player? = null

        fun getInstance(): Player {
            if (player == null) {
                player = Player()
            }
            return player!!
        }
    }

    private val mediaPlayer = MediaPlayer()

    /**
     * 设置网络资源
     */
    fun setNetworkSource(url: String) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepare()
    }

    /**
     * 设置本地资源
     */
    fun setLocalSource(source: FileDescriptor) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
        mediaPlayer.setDataSource(source)
        mediaPlayer.prepare()
    }

    fun play() {
        try {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }
        } catch (ex: Exception) {
            ex.log()
        }
    }

    fun pause() {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        } catch (ex: Exception) {
            ex.log()
        }
    }

    fun getCurrentPlayPosition() = mediaPlayer.currentPosition

    fun getCurrentPlayDuration() = mediaPlayer.duration

    fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    fun isPlaying() = mediaPlayer.isPlaying

    fun clearCurrentMedia() {
        if (mediaPlayer.isPlaying)
            mediaPlayer.stop()
        mediaPlayer.reset()
    }

    fun serviceHealthCheck() = true

    fun  setOnCompletionListener(callback: () -> Unit) {
        thenPlayerFinishedCallback = callback
        mediaPlayer.setOnCompletionListener {
            thenPlayerFinishedCallback()
        }
    }
}