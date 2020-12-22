package com.example.my_board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

        ListView listView;
        final ListViewAdapter adapter;
        Button Button_main_write;

        final ArrayList<String> title_arr = new ArrayList<String> ();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Content");

        //Adapter 생성
        adapter = new ListViewAdapter();

        //리스트뷰 참조 및 Adapter달기
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);

        database.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSanpshot : dataSnapshot.getChildren()){
                    String key = postSanpshot.getKey();
                    adapter.addItem(ContextCompat.getDrawable(MainActivity.this, R.drawable.icon_notice), key);
                    title_arr.add(key);
                }
                System.out.println("titlearr" + title_arr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        System.out.println("gggg" + title_arr);
//        for(String data : title_arr){
//            System.out.println("the data is" + data);
//            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.icon_notice), data);
//        }
//         첫 번째 아이템 추가.
//        adapter.addItem(ContextCompat.getDrawable(MainActivity.this, R.drawable.icon_notice),
//                "practice Day 1") ;
//        // 두 번째 아이템 추가.
//        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.icon_notice),
//                "Sunday", "OMG! Tomorrow is Monday!!") ;
//        // 세 번째 아이템 추가.
//        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.icon_notice),
//                "last week", "Tomorrow is the last week for 2020.9") ;


        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position) ;

                String titleStr = item.getBoard_title() ;
                Drawable iconDrawable = item.getBoard_icon() ;

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
    }
}