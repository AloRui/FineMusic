package com.example.finemusic.ui

import android.app.AlertDialog
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import com.example.finemusic.R
import com.example.finemusic.models.MusicInfo
import com.example.finemusic.models.MusicListInfo
import com.example.finemusic.models.MusicListInfoByIdInfo
import com.example.finemusic.music.MusicManager
import com.example.finemusic.utils.Base
import com.example.finemusic.utils.CommonAdapter
import com.example.finemusic.utils.get
import com.example.finemusic.utils.msg
import com.example.finemusic.utils.post
import com.example.finemusic.views.AddMusicToListDialog

class MusicListDetailActivity :
    Base(R.layout.activity_music_list_detail, musicListInfo.name, false) {

    lateinit var lsMusicList: MutableList<MusicInfo>


    companion object {
        lateinit var musicListInfo: MusicListInfo
    }

    override fun doInit() {
    }

    override fun loadData() {
        loadMusicList()

        loadMusicListInfoByUser()
    }

    override fun bindView() {
        R.id.tvMusicListName.v(musicListInfo.name)
        R.id.tvCreator.v("Creator: ${musicListInfo.creator}")
        R.id.tvDesc.v("Description: ${musicListInfo.description}")

        R.id.ivPlay.ck<ImageView> {
            addMusicToPlayList()
        }
    }

    override fun bindEvent() {
    }

    private fun addMusicToPlayList() {
        MusicManager.replaceMusicList(lsMusicList)
        MusicManager.playMusic(lsMusicList.first())
    }

    private fun loadMusicList() {
        "music/list/bylist/${musicListInfo.id}".get {
            lsMusicList = it.data
            bindMusicList(lsMusicList)
        }
    }

    private fun bindMusicList(lsData: MutableList<MusicInfo>) {
        R.id.ivList.find<ListView>().adapter = object : CommonAdapter<MusicInfo>(
            lsData, R.layout.item_music, this
        ) {
            override fun initView(item: MusicInfo, itemView: View, pos: Int) {
                R.id.tvMusicName.v("${(pos + 1)}. ${item.name}")
                R.id.tvSingerName.v(item.singerName)

                itemView.setOnClickListener {
                    MusicManager.insertMusic2List(item, 0)
                    MusicManager.playMusic(item)
                }

                R.id.ivAdd.ck<ImageView> {
                    openAddToMusicListDialog(item.id)
                }
            }
        }
    }

    fun onFollowMusicListClicked(view: View) {
        "musiclist/follow/tigger/${musicListInfo.id}".post<Boolean> {
            if (it.code == 1) {
                loadMusicListInfoByUser()
            } else {
                "Sorry follow failed, please try again later".msg()
            }
        }
    }

    private fun loadMusicListInfoByUser() {
        "musiclist/info/aboutuser/${musicListInfo.id}".get<MusicListInfoByIdInfo> {
            val ivFollowed = R.id.ivFollowed.find<ImageView>()

            if (it.data.isFollowed) {
                ivFollowed.setImageResource(R.drawable.baseline_star_24_selected)
            } else {
                ivFollowed.setImageResource(R.drawable.baseline_star_24)
            }
        }
    }

    fun openAddToMusicListDialog(musicId: Int) {
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