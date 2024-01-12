package com.example.finemusic.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.finemusic.storage.Shared.clear
import com.orhanobut.logger.AndroidLogAdapter
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

class MyApp : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var ctx: Context
        lateinit var toast: Toast
        lateinit var handler: Handler
        lateinit var threadPoolExecutor: ThreadPoolExecutor
    }

    override fun onCreate() {
        super.onCreate()

        ctx = applicationContext
        toast = Toast.makeText(ctx, "", Toast.LENGTH_SHORT)
        com.orhanobut.logger.Logger.addLogAdapter(AndroidLogAdapter())

        handler = Handler(Looper.getMainLooper())
        threadPoolExecutor = ThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS, LinkedBlockingQueue())
    }
}