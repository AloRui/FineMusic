package com.example.finemusic.services

import com.example.finemusic.Common
import com.example.finemusic.models.UpdateUserInfo
import com.example.finemusic.models.UserIdInfo
import com.example.finemusic.models.UserInfo
import com.example.finemusic.utils.get
import com.example.finemusic.utils.post
import com.google.android.material.internal.FlowLayout

object UserServices {
    fun updateUserInfo(callback: () -> Unit = {}) {
        "user/auth".get {
            if (it.code == 1)
                Common.userInfo = it.data
            callback.invoke()
        }
    }
}