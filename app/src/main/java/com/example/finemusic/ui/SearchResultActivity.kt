package com.example.finemusic.ui

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.finemusic.R
import com.example.finemusic.adapter.FragmentPagerAdapter
import com.example.finemusic.models.InputSearchInfo
import com.example.finemusic.models.SearchResultInfo
import com.example.finemusic.ui.frag.ListSearchResultFrag
import com.example.finemusic.ui.frag.MusicSearchResultFrag
import com.example.finemusic.ui.frag.SingerSearchResultFrag
import com.example.finemusic.utils.Base
import com.example.finemusic.utils.msg
import com.example.finemusic.utils.post
import com.google.android.material.tabs.TabLayout

class SearchResultActivity : Base(R.layout.activity_search_result, "Search Result") {

    companion object {
        lateinit var searchValue: String
    }

    override fun doInit() {
    }

    override fun loadData() {
        search()
    }

    override fun bindView() {
    }

    override fun bindEvent() {
        val vp = R.id.vp.find<ViewPager>()
        val tabLayout = R.id.tabLayout.find<TabLayout>()
        tabLayout.setupWithViewPager(vp)
    }

    lateinit var searchResult: MutableList<SearchResultInfo>

    private fun search() {
        "search".post(
            InputSearchInfo(searchValue)
        ) {
            this.searchResult = it.data
            bindVPData()
        }

    }

    private fun bindVPData() {
        val lsPages = mutableListOf<Pair<String, Fragment>>(
            Pair("Music", MusicSearchResultFrag(searchResult.filter { a->a.type == "Music" }.toMutableList())),
            Pair("Singer", SingerSearchResultFrag(searchResult.filter { a->a.type == "Singer" }.toMutableList())),
            Pair("List", ListSearchResultFrag(searchResult.filter { a->a.type == "List" }.toMutableList())),
        )

        R.id.vp.find<ViewPager>().adapter = FragmentPagerAdapter(
            lsPages, supportFragmentManager
        )
    }
}