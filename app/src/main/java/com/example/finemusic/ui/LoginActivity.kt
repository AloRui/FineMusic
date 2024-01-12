package com.example.finemusic.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import com.example.finemusic.Common
import com.example.finemusic.R
import com.example.finemusic.models.LoginInfo
import com.example.finemusic.models.LoginResultInfo
import com.example.finemusic.models.MusicListInfo
import com.example.finemusic.services.UserServices
import com.example.finemusic.utils.Base
import com.example.finemusic.utils.get
import com.example.finemusic.utils.msg
import com.example.finemusic.utils.post
import com.example.finemusic.views.VerificationCodeView
import java.util.Random

class LoginActivity : Base(R.layout.activity_login, "Login") {
    var errorTimes = 0

    override fun doInit() {
    }

    override fun loadData() {
        R.id.etPhone.v("13507660274")
        R.id.etPassword.v("239159")
    }

    override fun bindView() {
        "user/auth".get {
            if (it.code == 1) {
                Common.userInfo = it.data
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        loadVerificationCode()
    }

    override fun bindEvent() {
        R.id.verificationCodeView.find<VerificationCodeView>()
            .setFinishedListener(object : VerificationCodeView.IOnVerificationFinishedListener {
                override fun onVerificationFinished(result: Boolean) {
                    if (result) {
                        "Verification code is correct!".msg()
                        R.id.verificationCodeView.find<VerificationCodeView>().visibility =
                            View.INVISIBLE
                        errorTimes = 0
                    } else {
                        AlertDialog.Builder(this@LoginActivity).setTitle("Robot Checking")
                            .setMessage("Sorry verification code is wrong!").setPositiveButton(
                                "OK"
                            ) { _, _ -> loadVerificationCode() }.show()
                    }
                }
            })
    }

    fun onLoginClicked(view: View) {
        if (R.id.etPhone.isEmpty() || R.id.etPassword.isEmpty()) {
            "Sorry please input the login information!".msg()
            return
        }

        "user/login".post<LoginResultInfo>(LoginInfo(R.id.etPhone.v(), R.id.etPassword.v())) {
            if (it.code == 1) {
                loadUserInfo()
            } else {
                it.message.msg()
                errorTimes++
                if (errorTimes >= 3) {
                    loadVerificationCode()
                    R.id.verificationCodeView.find<VerificationCodeView>().visibility = View.VISIBLE
                }
            }
        }
    }

    fun loadUserInfo() {
        UserServices.updateUserInfo {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    val random = Random()

    private fun loadVerificationCode() {
        val code =
            "${random.nextInt(10)}${random.nextInt(10)}${random.nextInt(10)}${random.nextInt(10)}${
                random.nextInt(10)
            }"

        R.id.verificationCodeView.find<VerificationCodeView>().updateNumbers(code)
    }
}