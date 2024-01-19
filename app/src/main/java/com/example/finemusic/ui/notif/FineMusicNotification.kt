package com.example.finemusic.ui.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.finemusic.R
import com.example.finemusic.models.MusicInfo
import com.example.finemusic.music.FineMusicService
import com.example.finemusic.music.MusicManager
import com.example.finemusic.utils.MyApp

/**
 * FineMusic的通知栏
 */
class FineMusicNotification(private val ctx: Context) {

    private var isOpened = false

    private val CHANNEL_ID = "fine_music"

    private var notificationManager: NotificationManager =
        ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var remoteViews: RemoteViews = RemoteViews(ctx.packageName, R.layout.remote_music_play)

    private lateinit var notificationBuilder: NotificationCompat.Builder

    init {
        initNotificationBuilder()
        initNotificationChannel()
        setRemoteViewListener()
        setPlayModeChangeListener()
        bindRemoteViewClickEvent()
    }


    /**
     * 打开FineMusic的通知栏
     */
    fun openNotification() {
        isOpened = true
        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun initNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "fine_music_remote_view",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "channel_description"
        notificationManager.createNotificationChannel(channel)
    }

    private fun initNotificationBuilder() {
        this.notificationBuilder = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentTitle("Fine Music")
            .setContent(remoteViews)
    }

    private fun bindRemoteViewClickEvent() {
        val playPendingIntent = PendingIntent.getService(
            ctx,
            1,
            Intent(MyApp.ctx, FineMusicService::class.java).apply {
                action = "play"
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextMusicPendingIntent = PendingIntent.getService(
            ctx,
            1,
            Intent(MyApp.ctx, FineMusicService::class.java).apply {
                action = "next"
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val previousMusicPendingIntent = PendingIntent.getService(
            ctx,
            1,
            Intent(MyApp.ctx, FineMusicService::class.java).apply {
                action = "previous"
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val changePlayModePendingIntent = PendingIntent.getService(
            ctx,
            1,
            Intent(MyApp.ctx, FineMusicService::class.java).apply {
                action = "change_play_mode"
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        remoteViews.setOnClickPendingIntent(R.id.ivPlay, playPendingIntent)
        remoteViews.setOnClickPendingIntent(R.id.ivNext, nextMusicPendingIntent)
        remoteViews.setOnClickPendingIntent(R.id.ivPrevious, previousMusicPendingIntent)
        remoteViews.setOnClickPendingIntent(R.id.ivPlayMode, changePlayModePendingIntent)
    }

    private fun setRemoteViewListener() {
        MusicManager.setOnMusicPlayListener(object : MusicManager.OnMusicPlayListener {
            override fun onPlayPositionChanged(curPosition: Int, duration: Int, percent: Int) {
                val curTime = convertTime(curPosition)
                val durationTime = convertTime(duration)
                remoteViews.setTextViewText(R.id.tvMusicTime, "$curTime/$durationTime")
                updateRemoteViews()
            }

            override fun onPlayStatusChanged(isPlaying: Boolean) {
                val img = if (isPlaying)
                    R.drawable.remote_pause
                else
                    R.drawable.remote_play

                remoteViews.setImageViewResource(
                    R.id.ivPlay,
                    img
                )
                updateRemoteViews()
            }

            override fun onPlaySourceChange(musicInfo: MusicInfo) {
                remoteViews.setTextViewText(R.id.tvMusicName, musicInfo.name)
                updateRemoteViews()
            }
        })
    }

    private fun updateRemoteViews() {
        if (isOpened)
            notificationManager.notify(1, notificationBuilder.build())
    }

    private fun setPlayModeChangeListener() {
        MusicManager.setOnPlayModeChangeListener {
            when (it) {
                0 -> remoteViews.setImageViewResource(R.id.ivPlayMode, R.drawable.single)
                1 -> remoteViews.setImageViewResource(R.id.ivPlayMode, R.drawable.loop)
                2 -> remoteViews.setImageViewResource(R.id.ivPlayMode, R.drawable.random)
            }
            updateRemoteViews()
        }
    }

    private fun convertTime(time: Int): String {
        val min = time / 1000 / 60
        val sec = time / 1000 % 60
        return "${if (min < 10) "0$min" else min}:${if (sec < 10) "0$sec" else sec}"
    }
}