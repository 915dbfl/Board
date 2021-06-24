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
        val adapter = ListViewAdapter()
        listview.adapter = adapter
        var mainFragment = MainFragment()
        initNavigationBart()
        supportFragmentManager.beginTransaction().replace(R.id.main_content,mainFragment).commit()

        btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

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
                        var writeFragment = WriteFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_content,writeFragment).commit()
                    }
                    R.id.action_favorite_alarm -> {
                        var writeFragment = WriteFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_content,writeFragment).commit()
                    }
                    R.id.action_search -> {
                        var writeFragment = WriteFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_content,writeFragment).commit()
                    }
                }
                true
            }
        }
    }
}