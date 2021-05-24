package com.example.my_board.Activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.my_board.R
import com.example.my_board.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login.*

class LoginActivity : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        firebaseAuth = FirebaseAuth.getInstance()

        val user = applicationContext as User
        Button_login.setOnClickListener {
            if (TextUtils.isEmpty(TextInputEditText_id.text) || TextUtils.isEmpty(TextInputEditText_password.text)) {
                Toast.makeText(this@LoginActivity, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val id = TextInputEditText_id.text.toString().trim { it <= ' ' }
                val pw = TextInputEditText_password.text.toString().trim { it <= ' ' }
                firebaseAuth!!.signInWithEmailAndPassword(id, pw).addOnCompleteListener(this@LoginActivity, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val cuser = firebaseAuth!!.currentUser
                        user.setUId(cuser.email)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "로그인 오류", Toast.LENGTH_SHORT).show()
                        return@OnCompleteListener
                    }
                })
            }
        }
        Button_register.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}