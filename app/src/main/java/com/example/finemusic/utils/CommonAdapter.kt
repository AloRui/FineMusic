package com.example.finemusic.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.finemusic.models.MusicListInfo
import java.security.PrivateKey

abstract class CommonAdapter<T>(
    private val lsData: MutableList<T>,
    private val layoutId: Int,
    private val ctx: Context
) : BaseAdapter() {
    override fun getCount(): Int {
        return lsData.size
    }

    override fun getItem(position: Int): Any {
        return lsData[position] as Any
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    lateinit var itemView: View

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        this.itemView = LayoutInflater.from(ctx).inflate(layoutId, parent, false)
        initView(
            lsData[position], itemView, position
        )
        return itemView
    }

    protected abstract fun initView(item: T, itemView: View, pos: Int)

    inline fun <reified T : View> Int.find() = itemView.findViewById<T>(this)

    fun Int.v() = this.find<TextView>().text.toString().trim()

    inline fun <reified T : View> Int.ck(crossinline event: (view: T) -> Unit) {
        this.find<T>().setOnClickListener {
            event.invoke(it as T)
        }
    }

    fun Int.v(data: Any?) {
        this.find<TextView>().text = data?.toString() ?: ""
    }

    fun Int.isEmpty() = this.v().isEmpty()
}
