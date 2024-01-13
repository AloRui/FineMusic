package com.example.finemusic.ui.frag

import android.content.Intent
import android.view.View
import android.widget.ListView
import com.example.finemusic.R
import com.example.finemusic.models.SearchResultInfo
import com.example.finemusic.ui.MusicListDetailActivity
import com.example.finemusic.utils.BaseFragment
import com.example.finemusic.utils.CommonAdapter
import com.example.finemusic.utils.get

class ListSearchResultFrag(
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
            searchResultData, R.layout.item_search_result_list, requireContext()
        ) {
            override fun initView(item: SearchResultInfo, itemView: View, pos: Int) {
                R.id.tvName.v(item.name)
                R.id.tvSongs.v("Songs: ${item.detail["songs"]}")
                R.id.tvCreator.v("Created by: ${item.detail["creator"]}")

                itemView.setOnClickListener {
                    jumpToMusicListDetailActivity(
                        item.id
                    )
                }
            }
        }
    }

    private fun jumpToMusicListDetailActivity(musicListId: Int) {
        "musicList/info/byid/$musicListId".get {
            MusicListDetailActivity.musicListInfo = it.data
            startActivity(Intent(requireContext(), MusicListDetailActivity::class.java))
        }
    }
}