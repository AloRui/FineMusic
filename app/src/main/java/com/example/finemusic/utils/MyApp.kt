package com.example.finemusic.utils

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.finemusic.R
import com.example.finemusic.models.MusicInfo
import com.example.finemusic.music.FineMusicService
import com.example.finemusic.music.MusicManager
import com.example.finemusic.storage.Shared.getInt
import com.example.finemusic.ui.notif.FineMusicNotification
import com.orhanobut.logger.AndroidLogAdapter
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MyApp : Application() {

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as FineMusicService.FineMusicBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var ctx: Context
        lateinit var toast: Toast
        lateinit var handler: Handler
        lateinit var threadPoolExecutor: ThreadPoolExecutor

        @SuppressLint("StaticFieldLeak")
        lateinit var fineMusicNotification: FineMusicNotification

        private var mService: FineMusicService? = null

        private var mBound: Boolean = false

        fun getFineMusicService(): FineMusicService? {
            return mService
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate() {
        super.onCreate()

        ctx = applicationContext
        toast = Toast.makeText(ctx, "", Toast.LENGTH_SHORT)
        fineMusicNotification = FineMusicNotification(ctx)
        com.orhanobut.logger.Logger.addLogAdapter(AndroidLogAdapter())

        handler = Handler(Looper.getMainLooper())
        threadPoolExecutor = ThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS, LinkedBlockingQueue())

        runFineMusicService()

        MusicManager.setPlayerMode("play_mode".getInt(2))
    }

    private fun runFineMusicService() {
        Intent(this, FineMusicService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }
}