package com.example.my_board.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.example.my_board.R
import com.example.my_board.User
import com.google.firebase.auth.FirebaseAuth

internal class LoadingActivity : Activity() {
    var auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading)
        startLoading()
    }

    private fun startLoading() {
        val handler = Handler()
        handler.postDelayed({
            val cuser = auth.currentUser
            if (cuser != null) {
                val user = applicationContext as User
                if(cuser.email!! == null){
                    user.setUId(cuser.displayName.toString())
                }else{
                    user.setUId(cuser.email!!)
                    user.gender = "man"
                    user.job = "teacher"
                }
                startActivity(Intent(this@LoadingActivity, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@LoadingActivity, LoginActivity::class.java))
                finish()
            }
        }, 2000)
    }
}