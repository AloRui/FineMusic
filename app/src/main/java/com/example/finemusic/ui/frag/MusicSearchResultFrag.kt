package com.example.finemusic.ui.frag

import android.view.View
import android.widget.ImageView
import android.widget.ListView
import com.example.finemusic.R
import com.example.finemusic.models.SearchResultInfo
import com.example.finemusic.music.PlayerService
import com.example.finemusic.utils.BaseFragment
import com.example.finemusic.utils.CommonAdapter
import com.example.finemusic.utils.get

class MusicSearchResultFrag(
    private val searchResultData: MutableList<SearchResultInfo>
) : BaseFragment(R.layout.frag_search_result) {
    override fun doInit() {
    }

    override fun loadData() {
    }

    override fun bindView() {
        bindListView()
    }

    private fun bindListView() {
        val lvList = fragmentView.findViewById<ListView>(R.id.lvList)

        lvList.adapter = object : CommonAdapter<SearchResultInfo>(
            searchResultData, R.layout.item_search_result_music, requireContext()
        ) {
            override fun initView(item: SearchResultInfo, itemView: View, pos: Int) {
                R.id.tvName.v(item.name)
                R.id.tvSinger.v(item.detail["singer"])
                R.id.ivPlay.find<ImageView>().setOnClickListener {
                    playMusic(item.id)
                }
            }
        }
    }

    fun playMusic(musicId: Int) {
        "music/info/byid/$musicId".get {
            PlayerService.getInstance().apply {
                setPlayList(mutableListOf(Pair(it.data, 0)))
                playMusic()
            }
        }
    }
}