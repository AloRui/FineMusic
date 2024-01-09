package com.example.finemusic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.example.finemusic.models.UserInfo
import com.example.finemusic.utils.Base
import com.example.finemusic.utils.bindImg
import com.example.finemusic.utils.get
import com.example.finemusic.utils.msg

class MainActivity : Base(R.layout.activity_main, "FineMusic") {
    override fun doInit() {
    }

    override fun loadData() {
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
}
