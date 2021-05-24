package com.example.my_board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView;
        final ListViewAdapter adapter;
        Button Button_main_write;
        Button Button_logout = (Button)findViewById(R.id.Button_main_logout);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Content");

        //Adapter 생성
        adapter = new ListViewAdapter();
        final User user = (User)getApplication();

        //리스트뷰 참조 및 Adapter달기
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                adapter.setIsboard(1);

                for(DataSnapshot Snapshot : dataSnapshot.getChildren()){
                    String title = Snapshot.child("title").getValue().toString();
                    String content = Snapshot.child("content").getValue().toString();
                    String uid = Snapshot.child("uid").getValue().toString();
                    String countLike = Integer.toString((int)Snapshot.child("like").getChildrenCount());
                    adapter.addItem(ContextCompat.getDrawable(MainActivity.this, R.drawable.icon_notice), title, content, uid, countLike);

                }
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Failed to read value.", error.toException());
            }
        });

        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position) ;

                String titleStr = item.getBoard_title() ;
                String contentStr = item.getBoard_content();
                if(item.getBoard_uid().equals(user.getUId())){
                    Intent intent = new Intent(MainActivity.this, MyBoardActivity.class);
                    intent.putExtra("title", titleStr);
                    intent.putExtra("content", contentStr);
                    intent.putExtra("board_id", item.getBoard_uid()+titleStr);
                    intent.putExtra("countLike", item.getCountLike());
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(MainActivity.this, BoardActivity.class);
                    intent.putExtra("boardUid", item.getBoard_uid());
                    intent.putExtra("title", titleStr);
                    intent.putExtra("content", contentStr);
                    intent.putExtra("board_id", item.getBoard_uid()+titleStr);
                    intent.putExtra("countLike", item.getCountLike());
                    startActivity(intent);
                }


                // TODO : use item data.
            }
        }) ;

        Button_main_write = (Button)findViewById(R.id.Button_main_write);

        Button_main_write.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                startActivity(intent);
            }
        });

        Button_logout.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}