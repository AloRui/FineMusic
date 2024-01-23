package com.example.finemusic.ui

import android.graphics.Typeface
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.example.finemusic.R
import com.example.finemusic.models.MusicInfo
import com.example.finemusic.models.MusicLrcListInfo
import com.example.finemusic.music.MusicManager
import com.example.finemusic.utils.Base
import com.example.finemusic.utils.CommonAdapter
import com.example.finemusic.utils.get
import com.example.finemusic.utils.log
import com.example.finemusic.utils.msg

class LrcActivity : Base(
    R.layout.activity_lrc,
    musicInfo.name
) {

    private var myAdapter: CommonAdapter<MusicLrcListInfo>? = null

    private val lsData: MutableList<MusicLrcListInfo> = mutableListOf()

    private var curPos = 0f

    companion object {
        lateinit var musicInfo: MusicInfo
    }

    override fun doInit() {
    }

    override fun loadData() {
        loadLrcDataList()
    }

    override fun bindView() {
    }

    override fun bindEvent() {
        setPlayPosListener()

        MusicManager.setOnMusicPlayListener(object : MusicManager.OnMusicPlayListener {
            override fun onPlayPositionChanged(curPosition: Int, duration: Int, percent: Int) {
            }

            override fun onPlayStatusChanged(isPlaying: Boolean) {
            }

            override fun onPlaySourceChange(musicInfo: MusicInfo) {
                LrcActivity.musicInfo = musicInfo
                supportActionBar?.title = musicInfo.name
                loadLrcDataList()
            }
        })
    }

    private fun setPlayPosListener() {
        MusicManager.setOnMusicPlayListener(object : MusicManager.OnMusicPlayListener {
            override fun onPlayPositionChanged(curPosition: Int, duration: Int, percent: Int) {
                curPos = (curPosition / 1000f).log()
                if (lsData.any { a -> a.time > curPos - 1 && a.time < curPos + 1 }) {
                    myAdapter?.notifyDataSetChanged()
                }
            }

            override fun onPlayStatusChanged(isPlaying: Boolean) {
            }

            override fun onPlaySourceChange(musicInfo: MusicInfo) {
            }
        })
    }

    private fun loadLrcDataList() {
        "Music/lrc/byid/${musicInfo.id}".get<MutableList<MusicLrcListInfo>> {
            if (it.data.isEmpty()) {
                R.id.tvNoLrc.find<TextView>().visibility = View.VISIBLE
            } else {
                R.id.tvNoLrc.find<TextView>().visibility = View.GONE
            }

            lsData.clear()
            lsData.addAll(it.data.filter { a -> a.title.isNotEmpty() }.toMutableList())

            if (myAdapter == null)
                initAdapter()
            myAdapter?.notifyDataSetChanged()
        }
    }

    private fun initAdapter() {
        this.myAdapter = object : CommonAdapter<MusicLrcListInfo>(
            lsData,
            android.R.layout.simple_spinner_dropdown_item,
            this
        ) {
            override fun initView(item: MusicLrcListInfo, itemView: View, pos: Int) {
                (itemView as TextView).apply {
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    text = item.title
                }

                if (curPos >= item.time - 1 && curPos <= item.time + 1) {
                    itemView.setTextColor(resources.getColor(R.color.main))
                    itemView.typeface = Typeface.DEFAULT_BOLD
                    autoScroll(pos)
                } else {
                    itemView.setTextColor(resources.getColor(R.color.bg))
                    itemView.typeface = Typeface.DEFAULT
                }
            }
        }
        R.id.lvLrc.find<ListView>().adapter = myAdapter
    }

    private fun autoScroll(pos: Int) {
        val listView = R.id.lvLrc.find<ListView>()
        listView.smoothScrollToPositionFromTop(pos, 10, 200)
    }
}