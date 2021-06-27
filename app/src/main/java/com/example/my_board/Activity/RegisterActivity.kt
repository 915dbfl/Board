package com.example.my_board.Activity

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.my_board.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlinx.android.synthetic.main.register.*

class RegisterActivity : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
        firebaseAuth = FirebaseAuth.getInstance()
        var job : String = "student"
        var gender : String = "man"
        var character: String = gender + job
        gender_man.setOnClickListener(){
            if(gender_man.isChecked){
                gender = "man"
                if(job.equals("teacher")){
                    character = gender + job
                    character_image.setImageDrawable(resources.getDrawable(R.drawable.ic_teacher2) as BitmapDrawable)
                }else{
                    character = gender + job
                    character_image.setImageDrawable(resources.getDrawable(R.drawable.ic_student2) as BitmapDrawable)
                }
            }
        }
        gender_woman.setOnClickListener(){
            if(gender_woman.isChecked){
                gender = "woman"
                if(job.equals("teacher")){
                    character = gender + job
                    character_image.setImageDrawable(resources.getDrawable(R.drawable.ic_teacher1) as BitmapDrawable)
                }else{
                    character = gender + job
                    character_image.setImageDrawable(resources.getDrawable(R.drawable.ic_student1) as BitmapDrawable)
                }
            }
        }
        job_teacher.setOnClickListener(){
            if(job_teacher.isChecked){
                job = "teacher"
                if(gender.equals("woman")){
                    character = gender + job
                    character_image.setImageDrawable(resources.getDrawable(R.drawable.ic_teacher1) as BitmapDrawable)
                }else{
                    character = gender + job
                    character_image.setImageDrawable(resources.getDrawable(R.drawable.ic_teacher2) as BitmapDrawable)
                }
            }
        }
        job_student.setOnClickListener(){
            if(job_student.isChecked){
                job = "student"
                if(gender.equals("woman")){
                    character = gender + job
                    character_image.setImageDrawable(resources.getDrawable(R.drawable.ic_student1) as BitmapDrawable)
                }else{
                    character = gender + job
                    character_image.setImageDrawable(resources.getDrawable(R.drawable.ic_student2) as BitmapDrawable)
                }
            }
        }

        Button_register!!.setOnClickListener {
            if (TextUtils.isEmpty(TextView_register_id!!.text) || TextUtils.isEmpty(TextView_register_pw!!.text)) {
                Toast.makeText(this@RegisterActivity, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val email = TextView_register_id!!.text.toString()
                val pw = TextView_register_pw!!.text.toString()

                firebaseAuth!!.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(this@RegisterActivity, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth!!.currentUser
                        val num = user?.email?.indexOf("@")
                        val email = user?.email

                        val hashMap = HashMap<Any, String>()
                        hashMap["email"] = email!!
                        hashMap["pw"] = pw
                        hashMap["uid"] = user.email!!.substring(0, num!!)
                        hashMap["job"] = job
                        hashMap["gender"]= gender
                        hashMap["character"]= character
                        val database = FirebaseDatabase.getInstance()
                        val reference = database.getReference("User")
                        reference.child(user.email!!.substring(0, num)).setValue(hashMap)

                        //가입이 이루어졌을 시 가입 화면을 빠져나감.
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this@RegisterActivity, "회원가입을 성공하셨습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        if(pw.length < 6){
                            Toast.makeText(this@RegisterActivity, "패스워드는 최소 6자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@RegisterActivity, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show()
                        }
                        return@OnCompleteListener
                    }
                })
            }
        }
    }
}