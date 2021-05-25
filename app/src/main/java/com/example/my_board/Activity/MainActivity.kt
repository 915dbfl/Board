package com.example.my_board.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.my_board.*
import com.example.my_board.ListView.ListViewAdapter
import com.example.my_board.ListView.ListViewItem
import com.example.my_board.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val database = FirebaseDatabase.getInstance().reference.child("Content")
        val adapter = ListViewAdapter()
        val user = application as User

        listview.adapter = adapter
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                adapter.clear()
                adapter.setIsboard(1)
                for (Snapshot in dataSnapshot.children) {
                    val title = Snapshot.child("title").value.toString()
                    val content = Snapshot.child("content").value.toString()
                    val uid = Snapshot.child("uid").value.toString()
                    val countLike = Integer.toString(Snapshot.child("like").childrenCount.toInt())
                    adapter.addItem(ContextCompat.getDrawable(this@MainActivity, R.drawable.icon_notice), title, content, uid, countLike)
                }
                adapter.notifyDataSetChanged()
                listview.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Failed to read value.", error.toException())
            }
        })

        listview.onItemClickListener = OnItemClickListener { parent, v, position, id ->
            val item = parent.getItemAtPosition(position) as ListViewItem
            val titleStr = item.board_title
            val contentStr = item.board_content
            if (item.board_uid == user.uId) {
                val intent = Intent(this@MainActivity, MyBoardActivity::class.java)
                intent.putExtra("title", titleStr)
                intent.putExtra("content", contentStr)
                intent.putExtra("board_id", item.board_uid + titleStr)
                intent.putExtra("countLike", item.countLike)
                startActivity(intent)
            } else {
                val intent = Intent(this@MainActivity, BoardActivity::class.java)
                intent.putExtra("boardUid", item.board_uid)
                intent.putExtra("title", titleStr)
                intent.putExtra("content", contentStr)
                intent.putExtra("board_id", item.board_uid + titleStr)
                intent.putExtra("countLike", item.countLike)
                startActivity(intent)
            }
        }
        Button_main_write.setOnClickListener {
            val intent = Intent(this@MainActivity, WriteActivity::class.java)
            startActivity(intent)
        }
        Button_main_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}