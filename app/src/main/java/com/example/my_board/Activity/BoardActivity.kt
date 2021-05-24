package com.example.my_board.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.my_board.*
import com.example.my_board.ListView.ExListViewAdapter
import com.example.my_board.R
import com.google.firebase.database.*
import java.util.*
import kotlinx.android.synthetic.main.board.*

class BoardActivity : AppCompatActivity() {
    var adapter: ExListViewAdapter<*>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board)
        val listView = findViewById<View>(R.id.listview) as ExpandableListView
        val database = FirebaseDatabase.getInstance()
        val Ref = database.getReference("Content/")
        val user = application as User
        val intent = intent
        val board_id = intent.getStringExtra("board_id").toString()
        val title = intent.getStringExtra("title")
        val boardUid = intent.getStringExtra("boardUid")
        val content = intent.getStringExtra("content")
        countLike.text = intent.getStringExtra("countLike")
        likeImage.tag = "1"
        if (likeImage.tag.toString().toInt() == 1) {
            Ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (likeImage.tag.toString().toInt() == 1) {
                        if (dataSnapshot.child(boardUid + title + "/like/" + user.uId).getValue() != null) {
                            likeImage.tag = "0"
                            likeImage.isChecked = true
                            likeImage.tag = "2"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("Failed to read value.", error.toException())
                }
            })
        }
        board_title.text = title
        board_content.text = content
        Button_list.setOnClickListener {
            val intent = Intent(this@BoardActivity, MainActivity::class.java)
            startActivity(intent)
        }
        Button_done.setOnClickListener {
            val comment = TextInputEditText_comment.text.toString().trim { it <= ' ' }
            val user = application as User
            if (comment.isEmpty()) {
                Toast.makeText(this@BoardActivity, "댓글 작성 실패!", Toast.LENGTH_SHORT).show()
            } else {
                val hashMap = HashMap<Any, String>()
                hashMap["comment"] = comment
                hashMap["uid"] = user.uId!!
                val database = FirebaseDatabase.getInstance()
                val reference = database.getReference("Content/$board_id/")
                reference.child("comment/" + user.uId + comment + '/').setValue(hashMap)

                //글 작성 완료 시 가입 화면을 빠져나감.
                Toast.makeText(this@BoardActivity, "댓글 작성 완료!", Toast.LENGTH_SHORT).show()
            }
        }
        likeImage.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            var boardLike = intent.getStringExtra("countLike").toInt()
            var like = boardLike
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                if (isChecked) {
                    if (likeImage.tag.toString().toInt() == 1 || likeImage.tag.toString().toInt() == 2) {
                        like++
                        val likeRef = Ref.child(boardUid + title + "/like/" + user.uId)
                        likeRef.setValue(user.uId)
                        Toast.makeText(this@BoardActivity, "좋아요를 눌렀습니다.", Toast.LENGTH_SHORT).show()
                        countLike.text = Integer.toString(like)
                    }
                } else {
                    like--
                    val likeRef = Ref.child(boardUid + title + "/like/" + user.uId)
                    likeRef.removeValue()
                    Toast.makeText(this@BoardActivity, "좋아요를 취소했습니다.", Toast.LENGTH_SHORT).show()
                    countLike.text = Integer.toString(like)
                }
            }
        })
        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listView.setAdapter(null as BaseExpandableListAdapter?)
                val childlist = HashMap<ListViewItem, ArrayList<ListViewItem>?>()
                val parent = ArrayList<ListViewItem>()
                val adapter = ExListViewAdapter<Any?>(parent, childlist, listView)
                adapter.setUId(user.uId)
                adapter.setBoard_title(board_id)
                var index = 0
                val citemList = HashMap<Int, ArrayList<ListViewItem>>()
                for (parentSnapshot in dataSnapshot.child("$board_id/comment").children) {
                    index++
                    val child = ArrayList<ListViewItem>()
                    val commentContent = parentSnapshot.child("comment/").value.toString()
                    val uid = parentSnapshot.child("uid/").value.toString()
                    val pitem = ListViewItem(ContextCompat.getDrawable(this@BoardActivity, R.drawable.icon_notice), commentContent, uid)
                    parent.add(pitem)
                    for (childSnapshot in parentSnapshot.child("/ccomment").children) {
                        val ccommentContent = childSnapshot.child("comment/").value.toString()
                        println("댓글은 $commentContent, 대댓글은 $ccommentContent")
                        val cuid = childSnapshot.child("uid/").value.toString()
                        val citem = ListViewItem(ContextCompat.getDrawable(this@BoardActivity, R.drawable.icon_notice), ccommentContent, cuid)
                        child.add(citem)
                    }
                    citemList[index - 1] = child
                    if (!child.isEmpty()) {
                        childlist[parent[index - 1]] = citemList[index - 1]!!
                        println(parent[index - 1].board_title + "에 맵핑 완료!!!")
                    }
                }
                adapter.setmParentList(parent)
                adapter.setmChildHashMap(childlist)
                listView.setAdapter(adapter)
                val groupCount = adapter.getGroupCount()
                println("groundCount$groupCount")
                for (i in 0 until groupCount) {
                    listView.expandGroup(i)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Failed to read value.", error.toException())
            }
        })
    }
}