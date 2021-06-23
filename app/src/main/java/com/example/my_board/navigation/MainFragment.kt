package com.example.my_board.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.my_board.Activity.BoardActivity
import com.example.my_board.Activity.MyBoardActivity
import com.example.my_board.ListView.ListViewAdapter
import com.example.my_board.ListView.ListViewItem
import com.example.my_board.R
import com.example.my_board.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_detail.*

class MainFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.main_detail, container, false)
        val database = FirebaseDatabase.getInstance().reference.child("Content")
        val adapter = ListViewAdapter()
        val applicationContext = activity?.applicationContext
        val user : User = applicationContext as User

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                adapter.clear()
                adapter.setIsboard(1)
                for (Snapshot in dataSnapshot.children) {
                    val title = Snapshot.child("title").value.toString()
                    val content = Snapshot.child("content").value.toString()
                    val uid = Snapshot.child("uid").value.toString()
                    val countLike = Integer.toString(Snapshot.child("like").childrenCount.toInt())
                    adapter.addItem(context?.let { ContextCompat.getDrawable(it, R.drawable.icon_notice) }, title, content, uid, countLike)
                }
                adapter.notifyDataSetChanged()
                board_listview.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Failed to read value.", error.toException())
            }
        })
        //board_listview null로 처리되는거 해결해야 함.
        board_listview?.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val item = parent.getItemAtPosition(position) as ListViewItem
            val titleStr = item.board_title
            val contentStr = item.board_content
            val mainFragment = MainFragment()
            if (item.board_uid == user.uId) {
                val bundle = Bundle()
                val intent = Intent(context, MyBoardActivity::class.java)
                bundle.putString("title", titleStr)
                bundle.putString("content", contentStr)
                bundle.putString("board_id", item.board_uid + titleStr)
                bundle.putString("countLike", item.countLike)
                mainFragment.arguments = bundle
                startActivity(intent)

            } else {
                val bundle = Bundle()
                val intent = Intent(context, BoardActivity::class.java)
                bundle.putString("boardUid", item.board_uid)
                bundle.putString("title", titleStr)
                bundle.putString("content", contentStr)
                bundle.putString("board_id", item.board_uid + titleStr)
                bundle.putString("countLike", item.countLike)
                mainFragment.arguments = bundle
                startActivity(intent)
            }
        }
        return view
    }
}