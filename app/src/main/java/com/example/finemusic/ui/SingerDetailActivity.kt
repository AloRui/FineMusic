package com.example.finemusic.ui

import android.app.AlertDialog
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import com.example.finemusic.R
import com.example.finemusic.models.CollectionInfo
import com.example.finemusic.models.MusicInfo
import com.example.finemusic.models.SingerDetailInfo
import com.example.finemusic.music.MusicManager
import com.example.finemusic.utils.Base
import com.example.finemusic.utils.CommonAdapter
import com.example.finemusic.utils.get
import com.example.finemusic.views.AddMusicToListDialog

class SingerDetailActivity : Base(R.layout.activity_singer_detail, "Singer Detail") {

    companion object {
        var singerId: Int = -1
    }

    override fun doInit() {
    }

    override fun loadData() {
        loadSingerDetailInfo()
    }

    override fun bindView() {
    }

    override fun bindEvent() {
    }

    private fun loadSingerDetailInfo() {
        "Singer/detail/byid/$singerId".get<SingerDetailInfo> {
            it.data.apply {
                R.id.tvSingerName.v(name)
                R.id.tvBirthday.v("Birthday: $birthday")
                //可以通过ifEmpty来判断是否为空，如果为空则显示--
                R.id.tvDesc.v(
                    "Description: ${descriotion.ifEmpty { "--" }}"
                )
            }

            bindMusicList(R.id.lvTop10MusicList.find(), it.data.top10MusicList)
            bindCollectionList(it.data.myCollectionList)
        }
    }

    private fun bindMusicList(lv: ListView, data: MutableList<MusicInfo>) {
        lv.adapter = object : CommonAdapter<MusicInfo>(
            data, R.layout.item_top10_music, this@SingerDetailActivity
        ) {
            override fun initView(item: MusicInfo, itemView: View, pos: Int) {
                R.id.tvId.v((pos + 1).toString() + ".")
                R.id.tvName.v(item.name)
                R.id.tvSingerName.v(item.singerName)

                R.id.ivAdd.ck<ImageView> {
                    openAddToMusicListDialog(item.id)
                }

                itemView.setOnClickListener {
                    playMusic(item)
                }
            }
        }
    }

    private fun playMusic(musicInfo: MusicInfo) {
        MusicManager.insertMusic2List(musicInfo, 0)
        MusicManager.playMusic(musicInfo)
    }

    private fun bindCollectionList(data: MutableList<CollectionInfo>) {
        R.id.lvCollection.find<ListView>().adapter = object : CommonAdapter<CollectionInfo>(
            data, R.layout.item_collection, this@SingerDetailActivity
        ) {
            override fun initView(item: CollectionInfo, itemView: View, pos: Int) {
                R.id.tvCollectionName.v(item.name)
                bindMusicList(R.id.lvMusicList.find(), item.musicList)
            }
        }
    }

    private fun openAddToMusicListDialog(musicId: Int) {
        val dialogBuilder = AlertDialog.Builder(this)

        val dialog = dialogBuilder.create()

        val dialogView = AddMusicToListDialog(this).apply {
            putMusicId(musicId)
            setDialog(dialog)
        }.dialogView

        dialog.setView(dialogView)

        dialog.show()
    }
}