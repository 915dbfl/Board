package com.example.my_board.navigation

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.my_board.Activity.MainActivity
import com.example.my_board.R
import com.example.my_board.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.account.view.*
import kotlinx.android.synthetic.main.write.*
import kotlinx.android.synthetic.main.write.view.*
import java.util.HashMap

class AccountFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.account, container, false)
        val applicationContext = activity?.applicationContext
        val user : User = applicationContext as User
        view.userID.setText(user.uId)
        view.character_image.setImageDrawable(user.characterImage(user.gender+user.job))
        if(user.gender.equals("man")){
            view.gender_man.isChecked = true
        }else{
            view.gender_woman.isChecked = true
        }
        if(user.job.equals("teacher")){
            view.job_teacher.isChecked = true
        }else{
            view.job_student.isChecked = true
        }

        view.Button_modify.setOnClickListener(){
            val database = FirebaseDatabase.getInstance()
            val contentRef = database.getReference("User/"+user.uId)
            val gender = if(view.gender_man.isChecked) "man" else "woman"
            val job = if(view.job_teacher.isChecked) "teacher" else "student"
            val hashMap = HashMap<String, Any>()
            hashMap["gender"] = gender
            hashMap["job"] = job
            hashMap["character"]=gender+job
            contentRef.updateChildren(hashMap)
            user.gender = gender
            user.job = job
            view.character_image.setImageDrawable(user.characterImage(user.gender+user.job))
            Toast.makeText(context, "정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}