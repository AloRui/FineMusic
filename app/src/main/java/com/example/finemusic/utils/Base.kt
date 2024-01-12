package com.example.finemusic.utils

import android.graphics.BitmapFactory
import android.icu.text.CaseMap.Title
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.finemusic.ui.LoginActivity
import com.example.finemusic.models.RequestResultInfo
import com.example.finemusic.storage.Shared.getString
import com.example.finemusic.storage.Shared.setString
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import java.net.HttpURLConnection
import java.net.URL
import java.util.Scanner

val lsOpenedActivity = mutableListOf<AppCompatActivity>()

fun logout() {
    lsOpenedActivity.map {
        if (it !is LoginActivity) it.finish()
    }
}

abstract class Base(
    private val layoutId: Int,
    private val tl: String = "",
    private val canBack: Boolean = false
) : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layoutId)

        supportActionBar?.apply {
            title = tl
            setDisplayHomeAsUpEnabled(canBack)
            if (tl.isEmpty())
                hide()
        }

        doInit()
        loadData()
        bindView()
        bindEvent()

        lsOpenedActivity.add(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        lsOpenedActivity.remove(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return true
    }

    protected abstract fun doInit()
    protected abstract fun loadData()
    protected abstract fun bindView()
    protected abstract fun bindEvent()

    inline fun <reified T : View> Int.find() = findViewById<T>(this)

    fun Int.v() = this.find<TextView>().text.toString().trim()

    fun Int.v(data: Any?) {
        this.find<TextView>().text = data?.toString() ?: ""
    }

    fun Int.isEmpty() = this.v().isEmpty()

    inline fun <reified T : View> Int.ck(crossinline event: (v: View) -> Unit) {
        this.find<View>().setOnClickListener {
            event.invoke(it)
        }
    }
}

abstract class BaseFragment(
    private val layoutId: Int,
) : Fragment() {
    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = LayoutInflater.from(requireContext()).inflate(layoutId, null)
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doInit()
        loadData()
        bindView()
    }

    protected abstract fun doInit()
    protected abstract fun loadData()
    protected abstract fun bindView()
}

fun <T> T.msg(): T {
    MyApp.toast.apply {
        setText(this@msg.toString())
    }.show()
    return this
}

fun <T> T.log(): T {
    Logger.e(this@log.toString())
    return this
}

//#region 网络请求
inline fun <reified T> String.get(crossinline event: (data: RequestResultInfo<T>) -> Unit) {
    MyApp.threadPoolExecutor.execute {
        try {
            val address = "http://10.0.2.2:5120/api/${this@get}"
            val http = URL(address).openConnection() as HttpURLConnection
            http.requestMethod = "GET"
            val cookie = "cookie".getString()
            http.setRequestProperty("Cookie", cookie)
            val json = Scanner(http.inputStream).useDelimiter("\\A").next()
            val data = fromJson<T>(json)
            MyApp.handler.post {
                event.invoke(data)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

inline fun <reified T> String.post(
    params: Any? = null,
    crossinline event: (data: RequestResultInfo<T>) -> Unit
) {
    MyApp.threadPoolExecutor.execute {
        try {
            val address = "http://10.0.2.2:5120/api/${this@post}"
            val http = URL(address).openConnection() as HttpURLConnection
            http.requestMethod = "POST"
            http.doOutput = true

            val cookie = "cookie".getString()
            http.setRequestProperty("Cookie", cookie)

            http.setRequestProperty("Content-Type", "Application/JSON")

            if (params != null)
                http.outputStream.write(Gson().toJson(params).toByteArray())
            val json = Scanner(http.inputStream).useDelimiter("\\A").next()
            val data = fromJson<T>(json)

            if (this.lowercase() == "user/login") {
                val cookie = http.headerFields["Set-Cookie"]
                "cookie".setString(cookie?.joinToString(";") ?: "")
            }

            MyApp.handler.post {
                event.invoke(data)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

inline fun <reified T> fromJson(content: String): RequestResultInfo<T> {
    return Gson().fromJson(content, object : TypeToken<RequestResultInfo<T>>() {}.type)
}

fun ImageView.bindImg(fileName: String) {
    MyApp.threadPoolExecutor.execute {
        try {
            val bitmap =
                BitmapFactory.decodeStream(URL("http://10.0.2.2:5120/$fileName").openStream())
            MyApp.handler.post {
                this.setImageBitmap(bitmap)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

//#endregion