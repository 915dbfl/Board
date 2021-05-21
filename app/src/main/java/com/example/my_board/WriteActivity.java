package com.example.my_board;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class WriteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write);

        final TextView TextInputEditText_title, TextInputEditText_content;
        Button Button_write;

        TextInputEditText_title = (TextView) findViewById(R.id.TextInputEditText_title);
        TextInputEditText_content = (TextView) findViewById(R.id.TextInputEditText_content);
        Button_write = (Button) findViewById(R.id.Button_write);

        Intent intent = getIntent();
        final String btitle = intent.getStringExtra("title");
        final String bcontent = intent.getStringExtra("content");
        System.out.println(btitle + bcontent);
        final String board_id = intent.getStringExtra("board_id");

        final User user = (User)getApplication();
        if(btitle != null && bcontent != null){
            TextInputEditText_content.setText(bcontent);
            TextInputEditText_title.setText(btitle);
            Button_write.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    final String title = TextInputEditText_title.getText().toString().trim();
                    String content = TextInputEditText_content.getText().toString().trim();

                    if(title != "" && content != ""){
                        //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("uid", user.getUId());
                        hashMap.put("title", title);
                        hashMap.put("content", content);
                        hashMap.put("like", 0);

                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference contentRef = database.getReference("Content/");
                        if(btitle.equals(title)){
                            contentRef.child(user.getUId() + title).updateChildren(hashMap);
                        }
                        else{
                            contentRef.child(user.getUId() + title).setValue(hashMap);
                        }
                        contentRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot Snapshot : dataSnapshot.child(user.getUId() + btitle + "/comment/").getChildren()){
                                    String comment = Snapshot.child("comment").getValue().toString();
                                    String uid = Snapshot.child("uid").getValue().toString();

                                    HashMap<Object, String> hashMap = new HashMap<>();
                                    hashMap.put("comment", comment);
                                    hashMap.put("uid", uid);
                                    DatabaseReference reference = database.getReference("Content/" + user.getUId() + title + "/comment/" + uid + comment + "/");
                                    reference.setValue(hashMap);
                                    for(DataSnapshot childSnapshot : Snapshot.child("ccomment/").getChildren()){
                                        String cComment = Snapshot.getValue().toString();
                                        String cCommentId = Snapshot.child("uid").getValue().toString();

                                        HashMap<Object, String> cHashMap = new HashMap<>();
                                        cHashMap.put("comment", cComment);
                                        cHashMap.put("uid", cCommentId);
                                        DatabaseReference cReference = reference.child("ccomment/" + cCommentId + cComment + "/");
                                        reference.setValue(hashMap);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w("Failed to read value.", error.toException());
                            }
                        });
                        if(!btitle.equals(title)){
                            contentRef.child(user.getUId() + btitle+ "/").removeValue();
                        }

                        //가입이 이루어졌을 시 가입 화면을 빠져나감.
                        Toast.makeText(WriteActivity.this, "글이 게시되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(WriteActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(WriteActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            Button_write.setOnClickListener(new Button.OnClickListener(){

                @Override
                public void onClick(View v) {
                    String title = TextInputEditText_title.getText().toString().trim();
                    String content = TextInputEditText_content.getText().toString().trim();
                    System.out.println(title + "," + content);

                    if(title.isEmpty() || content.isEmpty()){
                        Toast.makeText(WriteActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("uid", user.getUId());
                        hashMap.put("title", title);
                        hashMap.put("content", content);
                        hashMap.put("like", 0);


                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("Content");
                        reference.child(user.getUId() + title).setValue(hashMap);

                        //가입이 이루어졌을 시 가입 화면을 빠져나감.
                        Toast.makeText(WriteActivity.this, "글이 게시되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(WriteActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }


}
