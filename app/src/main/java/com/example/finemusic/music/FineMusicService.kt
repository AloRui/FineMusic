package com.example.finemusic.music

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.finemusic.storage.Shared.getInt
import com.example.finemusic.storage.Shared.setInt
import com.example.finemusic.utils.log
import com.example.finemusic.utils.msg
import java.io.FileDescriptor

class FineMusicService : Service() {
    private val binder = FineMusicBinder()

    inner class FineMusicBinder : Binder() {
        fun getService() = this@FineMusicService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        "Fine Player Service is started!".log()

        if (intent != null) {
            handleIntent(intent!!)
        }

        return START_STICKY
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            "play" -> {
                switchPlayMusic()
            }

            "next" -> {
                onNextMusic()
            }

            "previous" -> {
                onPreviousMusic()
            }

            "change_play_mode" -> changePlayMode()
        }
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    fun serviceHealthCheck() = true

    private fun switchPlayMusic() {
        if (MusicManager.isPlaying()) {
            MusicManager.pausePlay()
        } else {
            MusicManager.getCurrentMusic()?.apply {
                MusicManager.playMusic(this@apply)
            }
        }
    }

    private fun onNextMusic() {
        MusicManager.nextMusic()
    }

    private fun onPreviousMusic() {
        MusicManager.prevMusic()
    }

    private fun changePlayMode() {
        val curMode = "play_mode".getInt(1)
        val newMode = (curMode + 1) % 3
        MusicManager.setPlayerMode(newMode)
    }
}