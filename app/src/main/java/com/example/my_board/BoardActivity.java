package com.example.my_board;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MyBoardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myboard);
        final ListViewAdapter adapter = new ListViewAdapter();
        final ListView listView;

        final FirebaseDatabase[] database = {FirebaseDatabase.getInstance()};
        DatabaseReference myRef = database[0].getReference("Content");


        final TextView board_title;
        Button Button_list;
        Button Button_delete;
        Button Button_done;
        Button Button_modify;

        TextView board_content;
        final TextView board_comment;

        Button_list = (Button) findViewById(R.id.Button_list);
        Button_modify = (Button) findViewById(R.id.Button_modify);
        Button_delete = (Button) findViewById(R.id.Button_delete);
        Button_done = (Button) findViewById(R.id.Button_done);

        board_title = (TextView)findViewById(R.id.board_title);
        board_content = (TextView)findViewById(R.id.board_content);
        board_comment = (TextView)findViewById(R.id.TextInputEditText_comment);
        listView = (ListView) findViewById(R.id.listview);

        Intent intent = getIntent();

        final String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        final int[] commentCount = {0};


        board_title.setText(title);
        board_content.setText(content);

        Button_list.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyBoardActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button_delete.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference removeContent = database.getReference("Content/" + title + '/');
                removeContent.removeValue();
                Intent intent = new Intent(MyBoardActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button_done.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                String comment = board_comment.getText().toString().trim();
                User user = (User)getApplication();
                if(comment != ""){
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("comment", comment);
                    hashMap.put("uid", user.getUId());
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("Content/" + title + "/");
                    reference.child("comment/" + comment + '/').setValue(hashMap);

                    //글 작성 완료 시 가입 화면을 빠져나감.
                    Toast.makeText(MyBoardActivity.this, "댓글 작성 완료!", Toast.LENGTH_SHORT).show();
                }

                else{
                    Toast.makeText(MyBoardActivity.this, "댓글 작성 실패!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button_modify.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        });



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                User user = (User)getApplication();
               // DataSnapshot commSanpshot = (DataSnapshot) dataSnapshot.child(title + "/comment").getChildren();

                for(DataSnapshot commentSnapshot : dataSnapshot.child(title + "/comment").getChildren()){
                    String commentContent = commentSnapshot.getValue().toString();
                    String uid = user.getUId();
                    adapter.addItem(ContextCompat.getDrawable(MyBoardActivity.this, R.drawable.icon_notice), commentContent, commentContent , uid);


//                    if(title.equals(key)){
//                        int count = (int)commentSnapshot.child("comment/").getChildrenCount();
//                        for(int i = 0; i < count; i++){
//                            String commentContent = commentSnapshot.child("comment/" + i + "/").getKey();
//                            System.out.println("commentContent: "+ commentContent);
//                            adapter.addItem(ContextCompat.getDrawable(BoardActivity.this, R.drawable.icon_notice), commentContent, key);
//                        }
//                    }
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

    }


}
