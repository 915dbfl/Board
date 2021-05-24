package com.example.my_board

import android.app.Application

class User : Application() {
    var uId: String? = null
        private set
    var upwd: String? = null
    var email: String? = null

    override fun onCreate() {
        super.onCreate()
    }

    fun setUId(uid: String) {
        val num = uid.indexOf("@")
        uId = uid.substring(0, num)
    }
}