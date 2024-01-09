package com.example.finemusic.storage

import android.content.Context
import com.example.finemusic.storage.Shared.setString
import com.example.finemusic.utils.MyApp

object Shared {
    fun String.getString(
        defaultValue: String = ""
    ): String {
        val shared = MyApp.ctx.getSharedPreferences("finemusic", Context.MODE_PRIVATE)
        val value = shared.getString(this, defaultValue)
        return value ?: defaultValue
    }

    fun String.setString(value: String) {
        val edit = MyApp.ctx.getSharedPreferences("finemusic", Context.MODE_PRIVATE).edit()
        edit.putString(this, value)
        edit.apply()
    }

    fun String.clear() {
        val edit = MyApp.ctx.getSharedPreferences("finemusic", Context.MODE_PRIVATE).edit()
        edit.remove(this)
        edit.apply()
    }
}