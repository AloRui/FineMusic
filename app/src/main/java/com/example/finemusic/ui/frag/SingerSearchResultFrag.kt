package com.example.finemusic.ui.frag

import android.view.View
import android.widget.ListView
import com.example.finemusic.R
import com.example.finemusic.models.SearchResultInfo
import com.example.finemusic.utils.BaseFragment
import com.example.finemusic.utils.CommonAdapter

class SingerSearchResultFrag(
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
            searchResultData, R.layout.item_search_result_singer, requireContext()
        ) {
            override fun initView(item: SearchResultInfo, itemView: View, pos: Int) {
                R.id.tvSingerName.v(item.name)
            }
        }
    }

}