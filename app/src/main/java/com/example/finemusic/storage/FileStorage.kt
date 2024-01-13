package com.example.finemusic.storage

import android.content.Context
import android.os.Environment
import com.example.finemusic.storage.Shared.getString
import com.example.finemusic.storage.Shared.setString
import com.example.finemusic.utils.MyApp
import com.example.finemusic.utils.log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.OutputStreamWriter
import java.util.UUID


object FileStorage {
    private val fileNameMap = mutableMapOf<Int, String>()

    init {
        "filemap".getString("[]").apply {
            fileNameMap.putAll(
                Gson().fromJson(
                    this,
                    object : TypeToken<MutableMap<Int, String>>() {}.type
                )
            )
        }
    }

    fun findFileNameFromFileId(fileId: Int): String {
        return fileNameMap[fileId] ?: ""
    }

    fun saveMusicFileToLocation(fileId: Int, fileData: ByteArray) {
        val fileName = UUID.randomUUID().toString() + ".mp3"
        putFileMapData(fileId, fileName)
        val saveResult = writeFileToLocation(fileName, fileData)
        if (!saveResult) {
            removeFileItemFromFileMap(fileId)
        }
    }

    private fun putFileMapData(fileId: Int, fileName: String) {
        fileNameMap.put(fileId, fileName)
        "filemap".setString(Gson().toJson(fileNameMap))
    }

    private fun removeFileItemFromFileMap(fileId: Int) {
        fileNameMap.remove(fileId)
        "filemap".setString(Gson().toJson(fileNameMap))
    }

    private fun writeFileToLocation(fileName: String, fileData: ByteArray): Boolean {
        try {
            val fileOutputStream = MyApp.ctx.openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write(fileData)
            fileOutputStream.flush()
            fileOutputStream.close()
            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
            return false
        }
    }
}