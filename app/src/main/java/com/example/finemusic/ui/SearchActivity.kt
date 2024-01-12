package com.example.finemusic.ui

import android.content.Intent
import android.view.View
import com.example.finemusic.R
import com.example.finemusic.utils.Base
import com.example.finemusic.utils.get
import com.example.finemusic.utils.msg
import com.example.finemusic.utils.post
import com.example.finemusic.views.FlowLayout

class SearchActivity : Base(R.layout.activity_search) {
    override fun doInit() {
    }

    override fun loadData() {
    }

    override fun bindView() {
    }

    override fun bindEvent() {
        R.id.flowHotWords.find<FlowLayout>()
            .setItemSelectedListener(onHistoryItemClicked)
        R.id.flowSearchHistory.find<FlowLayout>()
            .setItemSelectedListener(onHistoryItemClicked)
    }

    private val onHistoryItemClicked = object : FlowLayout.OnItemSelectedListener {
        override fun onItemSelected(value: String) {
            SearchResultActivity.searchValue = value
            startActivity(Intent(this@SearchActivity, SearchResultActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        loadHotWords()
        loadSearchHistory()
    }

    private fun loadHotWords() {
        val flowLayout = R.id.flowHotWords.find<com.example.finemusic.views.FlowLayout>()
        "search/history/hot".get {
            flowLayout.setItems(it.data)
        }
    }

    private fun loadSearchHistory() {
        val flowLayout = R.id.flowSearchHistory.find<com.example.finemusic.views.FlowLayout>()
        "search/history/owner".get {
            flowLayout.setItems(it.data)
        }
    }

    fun onSearchClicked(view: View) {
        if (R.id.etSearch.isEmpty()) {
            "Sorry please input the search field!".msg()
            return
        }

        SearchResultActivity.searchValue = R.id.etSearch.v()
        startActivity(Intent(this, SearchResultActivity::class.java))
    }

    fun onRemoveRecordsClicked(view: View) {
        "search/history/clear/byuser".get<Boolean> {
            if (it.code == 1) {
                "Clear search history successfully!".msg()
                loadSearchHistory()
                loadHotWords()
            } else {
                "Sorry clear search history failed!".msg()
            }
        }
    }
}