package com.example.finemusic.ui.frag

import android.app.AlertDialog
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import com.example.finemusic.R
import com.example.finemusic.models.SearchResultInfo
import com.example.finemusic.music.MusicManager
import com.example.finemusic.utils.BaseFragment
import com.example.finemusic.utils.CommonAdapter
import com.example.finemusic.utils.get
import com.example.finemusic.views.AddMusicToListDialog

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

                R.id.ivAdd.find<ImageView>().setOnClickListener {
                    openAddToMusicListDialog(item.id)
                }
            }
        }
    }

    fun playMusic(musicId: Int) {
        "music/info/byid/$musicId".get {
            MusicManager.insertMusic2List(it.data, 0)
            MusicManager.playMusic(it.data)
        }
    }

    fun openAddToMusicListDialog(musicId: Int) {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        val dialog = dialogBuilder.create()

        val dialogView = AddMusicToListDialog(requireContext()).apply {
            putMusicId(musicId)
            setDialog(dialog)
        }.dialogView

        dialog.setView(dialogView)

        dialog.show()
    }
}