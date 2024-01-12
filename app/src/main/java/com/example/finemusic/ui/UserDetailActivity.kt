package com.example.finemusic.ui

import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.drawToBitmap
import androidx.core.widget.addTextChangedListener
import com.example.finemusic.Common
import com.example.finemusic.R
import com.example.finemusic.models.UpdateUserInfo
import com.example.finemusic.models.UpdateUserPhotoInfo
import com.example.finemusic.models.UserIdInfo
import com.example.finemusic.services.UserServices
import com.example.finemusic.storage.Shared.clear
import com.example.finemusic.utils.Base
import com.example.finemusic.utils.bindImg
import com.example.finemusic.utils.bitmap2Base64
import com.example.finemusic.utils.logout
import com.example.finemusic.utils.msg
import com.example.finemusic.utils.post

class UserDetailActivity : Base(R.layout.activity_user_detail, "User Detail", true) {
    var isUpdatePhoto: Boolean = false

    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val bitmap = it.data!!.extras!!.get("data") as Bitmap
                R.id.ivPhoto.find<ImageView>().setImageBitmap(bitmap)
                isUpdatePhoto = true
            }
        }

    private val selectPhotoFromAlbumLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val bitmap = it.data!!.extras!!.get("data") as Bitmap
                R.id.ivPhoto.find<ImageView>().setImageBitmap(bitmap)
                isUpdatePhoto = true
            }
        }


    override fun doInit() {
    }

    override fun loadData() {
    }

    override fun bindView() {
        R.id.etNiceName.v(Common.userInfo.nicename)
        R.id.etSlogan.v(Common.userInfo.slogan)
        R.id.tvCharcaseNumber.v(Common.userInfo.slogan.length.toString() + "/50")
        if (Common.userInfo.photo.isNotEmpty()) {
            R.id.ivPhoto.find<ImageView>().bindImg("uphoto/${Common.userInfo.photo}")
        }
    }

    override fun bindEvent() {
        R.id.etSlogan.find<EditText>().addTextChangedListener {
            R.id.tvCharcaseNumber.v("${it!!.length}/50")
        }
    }

    fun onLogoutClicked(view: View) {
        "cookie".clear()
        logout()
    }

    fun onSave(view: View) {
        if (R.id.etNiceName.isEmpty()) {
            "Sorry please input the nicename field!".msg()
            return
        }

        val updateInfo = UpdateUserInfo(
            R.id.etNiceName.v(),
            R.id.etSlogan.v()
        )

        "user/info/update".post<UserIdInfo>(updateInfo)
        {
            if (it.code == 1) {
                if (isUpdatePhoto) {
                    updateUserPhoto()
                } else {
                    "Update userinfo successfully!".msg()
                    UserServices.updateUserInfo()
                }
            } else {
                "Sorry update userinfo failed!".msg()
            }
        }
    }

    fun updateUserPhoto() {
        val bitmap = R.id.ivPhoto.find<ImageView>().drawToBitmap()
        val base64 = bitmap.bitmap2Base64()
        val updateInfo = UpdateUserPhotoInfo(base64)

        "user/photo/update".post<UserIdInfo>(updateInfo)
        {
            if (it.code == 1) {
                "Update user info successfully!".msg()
                UserServices.updateUserInfo()
            } else {
                "Sorry update user info failed!".msg()
            }
        }
    }

    fun onSelectNewPhotoClicked(view: View) {
        val popupMenu = PopupMenu(this@UserDetailActivity, view)
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