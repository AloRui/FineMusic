package com.example.finemusic.views

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import com.example.finemusic.R
import com.example.finemusic.models.AddMusicToListInfo
import com.example.finemusic.models.MusicInfo
import com.example.finemusic.models.MusicListInfo
import com.example.finemusic.utils.get
import com.example.finemusic.utils.msg
import com.example.finemusic.utils.post
import com.google.gson.annotations.Until

class AddMusicToListDialog : LinearLayout {
    lateinit var dialogView: View

    lateinit var spMusicList: Spinner
    lateinit var btnSubmit: Button

    var musicId: Int = -1

    constructor(context: Context?) : super(context) {
        init(context!!)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context!!)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context!!)
    }

    fun putMusicId(musicId: Int): AddMusicToListDialog {
        this.musicId = musicId
        return this
    }

    var myDialog: AlertDialog? = null

    fun setDialog(dialog: AlertDialog) {
        this.myDialog = dialog
    }

    private fun init(ctx: Context) {
        this.dialogView =
            LayoutInflater.from(ctx).inflate(R.layout.dialog_add_music_to_list, this, true)

        spMusicList = dialogView.findViewById(R.id.spMusicList)
        btnSubmit = dialogView.findViewById(R.id.btnSubmit)

        loadMusicList()

        btnSubmit.setOnClickListener {
            addMusicToList()
        }
    }

    private fun loadMusicList() {
        "musiclist/list/owner".get<MutableList<MusicListInfo>> {
            spMusicList.adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                it.data
            )
        }
    }

    private fun addMusicToList() {
        val spId = (spMusicList.selectedItem as MusicListInfo).id

        val newInfo = AddMusicToListInfo(musicId, spId)

        "music/list/add".post<Boolean>(newInfo) {
            if (it.code == 1) {
                "Add to list success!".msg()
                this.myDialog?.dismiss()
            } else {
                "Sorry add must to list failed!".msg()
            }
        }
    }
}