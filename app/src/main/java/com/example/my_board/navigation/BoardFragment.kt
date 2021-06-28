package com.example.my_board.navigation

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.my_board.*
import com.example.my_board.Activity.MainActivity
import com.example.my_board.ListView.ExListViewAdapter
import com.example.my_board.ListView.ListViewItem
import com.example.my_board.R
import com.google.firebase.database.*
import java.util.*
import kotlinx.android.synthetic.main.board.*
import kotlinx.android.synthetic.main.board.TextInputEditText_comment
import kotlinx.android.synthetic.main.board.view.*
import kotlinx.android.synthetic.main.myboard.*


class BoardFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        var view = LayoutInflater.from(activity).inflate(R.layout.board, container, false)
        val database = FirebaseDatabase.getInstance()
        val Ref = database.getReference("Content/")
        val applicationContext = activity?.applicationContext
        val user : User = applicationContext as User
        val board_id = arguments!!.getString("board_id")
        val title = arguments!!.getString("title")
        val boardUid = arguments!!.getString("boardUid")
        val content = arguments!!.getString("content")
        view.countLike.text = arguments!!.getString("countLike")
        view.likeImage.tag = "1"
        if (view.likeImage.tag.toString().toInt() == 1) {
            Ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (view.likeImage.tag.toString().toInt() == 1) {
                        if (dataSnapshot.child(boardUid + title + "/like/" + user.uId).getValue() != null) {
                            view.likeImage.tag = "0"
                            view.likeImage.isChecked = true
                            view.likeImage.tag = "2"
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("Failed to read value.", error.toException())
                }
            })
        }
        view.board_title.text = title
        view.board_content.text = content
        view.Button_list.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
        view.Button_done.setOnClickListener {
            val comment = TextInputEditText_comment.text.toString().trim { it <= ' ' }
            val applicationContext = activity?.applicationContext
            val user : User = applicationContext as User
            if (comment.isEmpty()) {
                Toast.makeText(context, "댓글 작성 실패!", Toast.LENGTH_SHORT).show()
            } else {
                val hashMap = HashMap<Any, String>()
                hashMap["comment"] = comment
                hashMap["uid"] = user.uId!!
                hashMap["character"] = user.gender+user.job
                val database = FirebaseDatabase.getInstance()
                val reference = database.getReference("Content/$board_id/")
                reference.child("comment/" + user.uId + comment + '/').setValue(hashMap)

                //글 작성 완료 시 가입 화면을 빠져나감.
                Toast.makeText(context, "댓글 작성 완료!", Toast.LENGTH_SHORT).show()
            }
        }
        view.likeImage.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            var boardLike = arguments?.getString("countLike")!!.toInt()
            var like = boardLike
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                if (isChecked) {
                    if (view.likeImage.tag.toString().toInt() == 1 || view.likeImage.tag.toString().toInt() == 2) {
                        like++
                        val likeRef = Ref.child(boardUid + title + "/like/" + user.uId)
                        likeRef.setValue(user.uId)
                        Toast.makeText(context, "좋아요를 눌렀습니다.", Toast.LENGTH_SHORT).show()
                        view.countLike.text = Integer.toString(like)
                    }
                } else {
                    like--
                    val likeRef = Ref.child(boardUid + title + "/like/" + user.uId)
                    likeRef.removeValue()
                    Toast.makeText(context, "좋아요를 취소했습니다.", Toast.LENGTH_SHORT).show()
                    view.countLike.text = Integer.toString(like)
                }
            }
        })
        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                view.comment_listview.setAdapter(null as BaseExpandableListAdapter?)
                val childlist = HashMap<ListViewItem, ArrayList<ListViewItem>?>()
                val parent = ArrayList<ListViewItem>()
                val adapter = ExListViewAdapter(parent, childlist, view.comment_listview)
                adapter.setUId(user.uId)
                adapter.setCharacter(user.gender+user.job)
                adapter.setBoard_title(board_id)
                var index = 0
                val citemList = HashMap<Int, ArrayList<ListViewItem>>()
                for (parentSnapshot in dataSnapshot.child("$board_id/comment").children) {
                    index++
                    val child = ArrayList<ListViewItem>()
                    val commentContent = parentSnapshot.child("comment/").value.toString()
                    val uid = parentSnapshot.child("uid/").value.toString()
                    val character = parentSnapshot.child("character/").value.toString()
                    Log.d("===========================================", commentContent + uid + character)
                    val pitem = ListViewItem(commentContent, uid, user.characterImage(character))
                    parent.add(pitem)
                    for (childSnapshot in parentSnapshot.child("/ccomment").children) {
                        val ccommentContent = childSnapshot.child("comment/").value.toString()
                        val cuid = childSnapshot.child("uid/").value.toString()
                        val ccharacter = childSnapshot.child("character/").value.toString()
                        val citem = ListViewItem(ccommentContent, cuid, user.characterImage(ccharacter))
                        child.add(citem)
                    }
                    citemList[index - 1] = child
                    if (!child.isEmpty()) {
                        childlist[parent[index - 1]] = citemList[index - 1]!!
                    }
                }
                adapter.setmParentList(parent)
                adapter.setmChildHashMap(childlist)
                view.comment_listview.setAdapter(adapter)
                val groupCount = adapter.getGroupCount()
                println("groundCount$groupCount")
                for (i in 0 until groupCount) {
                    view.comment_listview.expandGroup(i)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Failed to read value.", error.toException())
            }
        })
        return view
    }
}