package com.example.finemusic.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.net.URL
import kotlin.concurrent.thread

fun Bitmap.bitmap2Base64(): String {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    val bytes = stream.toByteArray()
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}