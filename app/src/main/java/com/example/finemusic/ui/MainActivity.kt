package com.example.finemusic.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.RemoteViews
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.drawToBitmap
import com.example.finemusic.Common
import com.example.finemusic.R
import com.example.finemusic.models.MusicListInfo
import com.example.finemusic.models.NewMusicListInfo
import com.example.finemusic.music.MusicManager
import com.example.finemusic.utils.Base
import com.example.finemusic.utils.CommonAdapter
import com.example.finemusic.utils.bindImg
import com.example.finemusic.utils.bitmap2Base64
import com.example.finemusic.utils.get
import com.example.finemusic.utils.log
import com.example.finemusic.utils.msg
import com.example.finemusic.utils.post


class MainActivity : Base(R.layout.activity_main, "FineMusic") {

    override fun doInit() {
    }

    override fun loadData() {
        loadOwnerList()
        loadFollowedList()

        MusicManager.serviceHealthCheck().log()
    }

    override fun bindView() {
        R.id.tvNiceName.v(Common.userInfo.nicename)
        R.id.tvPhone.v(Common.userInfo.phone)
        R.id.tvSlogan.v(Common.userInfo.slogan)

        if (Common.userInfo.photo.isNotEmpty()) {
            R.id.ivPhoto.find<ImageView>().bindImg("uphoto/${Common.userInfo.photo}")
        }
    }

    override fun bindEvent() {
    }

    fun onJumpToUserDetail(view: View) {
        startActivity(Intent(this, UserDetailActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return true
    }


    private fun loadOwnerList() {
        "musiclist/list/owner".get {
            R.id.lvOwnerList.find<ListView>().adapter = object : CommonAdapter<MusicListInfo>(
                it.data, R.layout.item_music_list, this@MainActivity
            ) {
                override fun initView(item: MusicListInfo, itemView: View, pos: Int) {
                    R.id.tvListName.v(item.name)
                    R.id.tvCount.v("Music count: ${item.musicCount}")
                    R.id.tvCreator.v("Createor : ${item.creator}")

                    itemView.setOnClickListener {
                        jumpToMusicListDetail(item)
                    }
                }
            }
        }
    }

    private fun loadFollowedList() {
        "musiclist/list/followed".get {
            R.id.lvFollowed.find<ListView>().adapter = object : CommonAdapter<MusicListInfo>(
                it.data, R.layout.item_music_list, this@MainActivity
            ) {
                override fun initView(item: MusicListInfo, itemView: View, pos: Int) {
                    R.id.tvListName.v(item.name)
                    R.id.tvCount.v("Music count: ${item.musicCount}")
                    R.id.tvCreator.v("Creator : ${item.creator}")

                    itemView.setOnClickListener {
                        jumpToMusicListDetail(item)
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.menuSerach) {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        return true
    }

    fun jumpToMusicListDetail(musicListINfo: MusicListInfo) {
        MusicListDetailActivity.musicListInfo = musicListINfo
        startActivity(Intent(this, MusicListDetailActivity::class.java))
    }

    override fun onResume() {
        super.onResume()

        loadOwnerList()
        loadFollowedList()
    }

    fun onAddNewMusicListClicked(view: View) {
        openCreateMusicListDialog()
    }

    private fun openCreateMusicListDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_music_list, null)

        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setView(dialogView)

        createNewMusicListDialog = dialogBuilder.create()

        createNewMusicListDialog?.show()

        bindAddMusicListDialogEvent(dialogView)
    }

    private var ivCover: ImageView? = null
    private var createNewMusicListDialog: Dialog? = null

    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val bitmap = it.data!!.extras!!.get("data") as Bitmap
                ivCover?.setImageBitmap(bitmap)
                ivCover?.visibility = View.VISIBLE
            }
        }

    private val selectPhotoFromAlbumLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val bitmap = it.data!!.extras!!.get("data") as Bitmap
                ivCover?.setImageBitmap(bitmap)
                ivCover?.visibility = View.VISIBLE
            }
        }

    private fun bindAddMusicListDialogEvent(dialogView: View) {
        val tvSelectImg = dialogView.findViewById<TextView>(R.id.tvSelectCover)
        this.ivCover = dialogView.findViewById(R.id.ivCover)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etDesc = dialogView.findViewById<EditText>(R.id.etDesc)

        tvSelectImg.setOnClickListener {
            openSelectImgPopMenu(it)
        }

        btnSave.setOnClickListener {
            onSubmitNewMusicList(etName.text.trim().toString(), etDesc.text.trim().toString())
        }
    }

    private fun onSubmitNewMusicList(name: String, desc: String) {
        if (name.isEmpty()) {
            "Sorry the music list name can't be empty!".msg()
            return
        }

        var cover: Bitmap? = null
        if (ivCover?.visibility == View.VISIBLE) cover =
            ivCover?.drawToBitmap(Bitmap.Config.ARGB_8888)

        val base64 = cover?.bitmap2Base64() ?: ""

        val newMusicInfo = NewMusicListInfo(name, base64, desc)

        "musiclist/new".post<Boolean>(newMusicInfo) {
            if (it.code == 1) {
                "Create new music list successfully!".msg()
                loadOwnerList()
                if (createNewMusicListDialog != null) createNewMusicListDialog?.dismiss()
            } else {
                "Sorry create new music list failed! Please try again!".msg()
            }
        }
    }

    private fun openSelectImgPopMenu(view: View) {
        val popupMenu = PopupMenu(this@MainActivity, view)
        popupMenu.inflate(R.menu.popupmenu)
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.itemTakePhoto -> onTakePhoto()
                R.id.itemSelectPhotoFromAlbum -> onSelectPhotoFromAlbum()
            }
            true
        }
    }

    private fun onTakePhoto() {
        takePhotoLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    private fun onSelectPhotoFromAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        selectPhotoFromAlbumLauncher.launch(intent)
    }
}
