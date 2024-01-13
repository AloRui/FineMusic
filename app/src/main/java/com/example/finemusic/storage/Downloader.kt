package com.example.finemusic.storage

import com.example.finemusic.utils.MyApp
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL


object Downloader {
     fun downloadMusicFile(
        fileName: String,
        event: (fileData: ByteArray) -> Unit
    ) {
        MyApp.threadPoolExecutor.execute {
            try {
                val address = "http://10.0.2.2:5120/music/$fileName"
                val url = URL(address)
                val httpURLConnection =
                    url.openConnection() as HttpURLConnection
                httpURLConnection.connectTimeout = 5 * 1000
                httpURLConnection.readTimeout = 60 * 1000
                httpURLConnection.requestMethod = "GET"
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive")
                val inputStream = httpURLConnection.inputStream
                val byteArrayOutputStream =
                    ByteArrayOutputStream()
                val buffer = ByteArray(4 * 1024)
                var len = 0
                while (inputStream.read(buffer).also { len = it } != -1) {
                    byteArrayOutputStream.write(buffer, 0, len)
                }
                byteArrayOutputStream.flush()
                val fileData = byteArrayOutputStream.toByteArray()
                MyApp.handler.post {
                    event.invoke(fileData)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

}