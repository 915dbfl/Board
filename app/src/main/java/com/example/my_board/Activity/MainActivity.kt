package com.example.my_board.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.my_board.*
import com.example.my_board.ListView.ListViewAdapter
import com.example.my_board.ListView.ListViewItem
import com.example.my_board.R
import com.example.my_board.navigation.BoardFragment
import com.example.my_board.navigation.MainFragment
import com.example.my_board.navigation.WriteFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val database = FirebaseDatabase.getInstance().reference.child("Content")
        val adapter = ListViewAdapter()
        val user = application as User
        listview.adapter = adapter
        var mainFragment = MainFragment()
        initNavigationBart()
        supportFragmentManager.beginTransaction().replace(R.id.main_content,mainFragment).commit()
//        database.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                adapter.clear()
//                adapter.setIsboard(1)
//                for (Snapshot in dataSnapshot.children) {
//                    val title = Snapshot.child("title").value.toString()
//                    val content = Snapshot.child("content").value.toString()
//                    val uid = Snapshot.child("uid").value.toString()
//                    val countLike = Integer.toString(Snapshot.child("like").childrenCount.toInt())
//                    adapter.addItem(ContextCompat.getDrawable(this@MainActivity, R.drawable.icon_notice), title, content, uid, countLike)
//                }
//                adapter.notifyDataSetChanged()
//                listview.adapter = adapter
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.w("Failed to read value.", error.toException())
//            }
//        })
//
//        listview.onItemClickListener = OnItemClickListener { parent, v, position, id ->
//            val item = parent.getItemAtPosition(position) as ListViewItem
//            val titleStr = item.board_title
//            val contentStr = item.board_content
//            if (item.board_uid == user.uId) {
//                val intent = Intent(this@MainActivity, MyBoardActivity::class.java)
//                intent.putExtra("title", titleStr)
//                intent.putExtra("content", contentStr)
//                intent.putExtra("board_id", item.board_uid + titleStr)
//                intent.putExtra("countLike", item.countLike)
//                startActivity(intent)
//            } else {
//                val boardFragment = BoardFragment()
//                val bundle = Bundle()
//                bundle.putString("boardUid", item.board_uid)
//                bundle.putString("title", titleStr)
//                bundle.putString("content", contentStr)
//                bundle.putString("board_id", item.board_uid + titleStr)
//                bundle.putString("countLike", item.countLike)
//                .arguments = bundle
//                supportFragmentManager.beginTransaction().replace(R.id.main_content,boardFragment).commit()
//            }
//        }
//
//        btn_logout.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//            val intent = Intent(this@MainActivity, LoginActivity::class.java)
//            startActivity(intent)
//        }
//
    }
    private fun initNavigationBart(){
        bottom_navigation.run {
            setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.action_home -> {
                        var mainFragment = MainFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_content,mainFragment).commit()
                    }
                    R.id.action_write -> {
                        var writeFragment = WriteFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_content,writeFragment).commit()
                    }
                    R.id.action_account -> {
                        val intent = Intent(this@MainActivity, WriteActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.action_favorite_alarm -> {
                        val intent = Intent(this@MainActivity, WriteActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.action_search -> {
                        val intent = Intent(this@MainActivity, WriteActivity::class.java)
                        startActivity(intent)
                    }
                }
                true
            }
        }
    }

    private fun changeFragment(fragment : Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit()
    }

}