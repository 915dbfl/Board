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
import androidx.fragment.app.Fragment
import com.example.my_board.Activity.MainActivity
import com.example.my_board.R
import com.example.my_board.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.write.*
import kotlinx.android.synthetic.main.write.view.*
import java.util.HashMap

class WriteFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.write, container, false)
        val applicationContext = activity?.applicationContext
        val user : User = applicationContext as User
        val intent = Intent()
        if (intent.getStringExtra("title") != null && intent.getStringExtra("content") != null) {
            val btitle = intent.getStringExtra("title").toString()
            val bcontent = intent.getStringExtra("content").toString()
            TextInputEditText_content.text = bcontent as Editable
            TextInputEditText_title.text = btitle as Editable
            Button_write.setOnClickListener {
                val title = TextInputEditText_title.text.toString().trim { it <= ' ' }
                val content = TextInputEditText_content.text.toString().trim { it <= ' ' }
                if (title !== "" && content !== "") {
                    //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
                    val hashMap = HashMap<String, Any>()
                    hashMap["uid"] = user.uId!!
                    hashMap["title"] = title
                    hashMap["content"] = content
                    val database = FirebaseDatabase.getInstance()
                    val contentRef = database.getReference("Content/")
                    if (btitle == title) {
                        val myRef = database.getReference("User/" + user.uId + "/likeList")
                        contentRef.child(user.uId + title).updateChildren(hashMap)
                    } else {
                        contentRef.child(user.uId + title).setValue(hashMap)
                    }
                    contentRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (Snapshot in dataSnapshot.child(user.uId + btitle + "/comment/").children) {
                                val comment = Snapshot.child("comment").value.toString()
                                val uid = Snapshot.child("uid").value.toString()
                                val hashMap = HashMap<Any, String>()
                                hashMap["comment"] = comment
                                hashMap["uid"] = uid
                                val reference = database.getReference("Content/" + user.uId + title + "/comment/" + uid + comment + "/")
                                reference.setValue(hashMap)
                                for (childSnapshot in Snapshot.child("ccomment/").children) {
                                    val cComment = Snapshot.value.toString()
                                    val cCommentId = Snapshot.child("uid").value.toString()
                                    val cHashMap = HashMap<Any, String>()
                                    cHashMap["comment"] = cComment
                                    cHashMap["uid"] = cCommentId
                                    val cReference = reference.child("ccomment/$cCommentId$cComment/")
                                    reference.setValue(hashMap)
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            Log.w("Failed to read value.", error.toException())
                        }
                    })
                    if (btitle != title) {
                        contentRef.child(user.uId + btitle + "/").removeValue()
                    }

                    //가입이 이루어졌을 시 가입 화면을 빠져나감.
                    Toast.makeText(context, "글이 게시되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                } else {
                    Toast.makeText(context, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            view.Button_write.setOnClickListener {
                val title = TextInputEditText_title.text.toString().trim { it <= ' ' }
                val content = TextInputEditText_content.text.toString().trim { it <= ' ' }
                println("$title,$content")
                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(context, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
                    val hashMap = HashMap<String, Any>()
                    hashMap["uid"] = user.uId!!
                    hashMap["title"] = title
                    hashMap["content"] = content
                    val database = FirebaseDatabase.getInstance()
                    val reference = database.getReference("Content")
                    reference.child(user.uId + title).setValue(hashMap)

                    //가입이 이루어졌을 시 가입 화면을 빠져나감.
                    Toast.makeText(context, "글이 게시되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                }
            }
        }
        fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
        return view
    }
}