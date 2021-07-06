package com.example.my_board.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.example.my_board.R
import com.example.my_board.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
                    user.setUId(cuser!!.email!!)
                    val database = FirebaseDatabase.getInstance()
                    val refUser = database.getReference("User/"+user.uId)
                    refUser.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            user.gender = dataSnapshot.child("/gender").getValue().toString()
                            user.job = dataSnapshot.child("/job").getValue().toString()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            user.gender = "man"
                            user.job = "teacher"
                        }
                    })
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