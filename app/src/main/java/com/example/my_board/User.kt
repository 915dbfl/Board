package com.example.my_board

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.res.ResourcesCompat

class User : Application() {
    var uId: String? = null
        private set
    var gender: String = "man"
    var job: String = "teacher"
    lateinit var icon: BitmapDrawable

    override fun onCreate() {
        super.onCreate()
        if(gender.equals("man")){
            if(job.equals("teacher")){
                icon = resources.getDrawable(R.drawable.ic_teacher2) as BitmapDrawable
            }else {
                icon = resources.getDrawable(R.drawable.ic_student2) as BitmapDrawable
            }
        }else{
            if(job.equals("teacher")){
                icon = resources.getDrawable(R.drawable.ic_teacher1) as BitmapDrawable
            }else{
                icon = resources.getDrawable(R.drawable.ic_student1) as BitmapDrawable
            }
        }
    }

    fun setUId(uid: String) {
        val num = uid.indexOf("@")
        if(num == -1){
            Log.d("=========================================uid", uid)
            uId = uid
        } else{
            uId = uid.substring(0, num)
        }
    }

    fun characterImage(character: String): BitmapDrawable {
        if(character.equals("womanstudent")){
            return ResourcesCompat.getDrawable(resources, R.drawable.ic_student1, null) as BitmapDrawable
        }else if(character.equals("manstudent")){
            return ResourcesCompat.getDrawable(resources, R.drawable.ic_student2, null) as BitmapDrawable
        }else if(character.equals("womanteacher")){
            return ResourcesCompat.getDrawable(resources, R.drawable.ic_teacher1, null) as BitmapDrawable
        }else{
            return ResourcesCompat.getDrawable(resources, R.drawable.ic_teacher2, null) as BitmapDrawable
        }
    }
}