package com.example.my_board;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class BoardActivity extends AppCompatActivity {
    public ExListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        final ExpandableListView listView = (ExpandableListView) findViewById(R.id.listview);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference Ref = database.getReference("Content/");
        final User user = (User)getApplication();

        final TextView board_title;
        Button Button_list;
        Button Button_delete;
        Button Button_done;
        Button Button_modify;

        TextView board_content;
        final TextView board_comment;
        final TextView countLike;
        final ToggleButton likeImage;

        Button_list = (Button) findViewById(R.id.Button_list);
        Button_done = (Button) findViewById(R.id.Button_done);

        board_title = (TextView)findViewById(R.id.board_title);
        board_content = (TextView)findViewById(R.id.board_content);
        board_comment = (TextView)findViewById(R.id.TextInputEditText_comment);
        likeImage = (ToggleButton)findViewById(R.id.likeImage);
        countLike = (TextView)findViewById(R.id.countLike);

        final Intent intent = getIntent();
        final String board_id = intent.getStringExtra("board_id");
        final String title = intent.getStringExtra("title");
        final String boardUid = intent.getStringExtra("boardUid");
        String content = intent.getStringExtra("content");
        countLike.setText(intent.getStringExtra("countLike"));

        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        final DatabaseReference myRef = database.getReference("User/" + user.getUId() + "/likeList");
        likeImage.setTag("1");
        if(Integer.parseInt(likeImage.getTag().toString()) == 1){
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(Integer.parseInt(likeImage.getTag().toString()) == 1){
                        if(dataSnapshot.child(title).getValue() != null){
                            likeImage.setTag("0");
                            likeImage.setChecked(true);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Failed to read value.", error.toException());
                }
            });
        }


        board_title.setText(title);
        board_content.setText(content);

        Button_list.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoardActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        Button_done.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                String comment = board_comment.getText().toString().trim();
                User user = (User)getApplication();
                if(comment.isEmpty()){
                    Toast.makeText(BoardActivity.this, "댓글 작성 실패!", Toast.LENGTH_SHORT).show();
                }
                else{
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("comment", comment);
                    hashMap.put("uid", user.getUId());
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("Content/" + board_id + "/");
                    reference.child("comment/" + user.getUId() + comment + '/').setValue(hashMap);

                    //글 작성 완료 시 가입 화면을 빠져나감.
                    Toast.makeText(BoardActivity.this, "댓글 작성 완료!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        likeImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            int boardLike = Integer.parseInt(intent.getStringExtra("countLike"));
            int like = boardLike;
            SharedPreferences preferencesR = getPreferences(MODE_PRIVATE);
            boolean likePrefR = preferencesR.getBoolean(title + "like", false);
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println("likeRefR" + likePrefR);
                if(isChecked)
                {
                    if(Integer.parseInt(likeImage.getTag().toString()) == 1) {
                        like++;
                        DatabaseReference likeRef = Ref.child(boardUid + title + "/like/");
                        likeRef.setValue(like);
                        Toast.makeText(BoardActivity.this, "좋아요를 눌렀습니다.", Toast.LENGTH_SHORT).show();
                        countLike.setText(Integer.toString(like));
                        myRef.child(title+"/").setValue(title);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(title + "like", true); // value to store
                        editor.commit();
                    }
                }
                else
                {
                    like--;
                    likeImage.setChecked(false);
                    DatabaseReference likeRef = Ref.child(boardUid+ title + "/like/");
                    likeRef.setValue(like);
                    Toast.makeText(BoardActivity.this, "좋아요를 취소했습니다.", Toast.LENGTH_SHORT).show();
                    countLike.setText(Integer.toString(like));
                    myRef.child(title+"/").removeValue();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(title + "like", false); // value to store
                    editor.commit();
                }
            }
        });


        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listView.setAdapter((BaseExpandableListAdapter)null);
                adapter = new ExListViewAdapter(null, null, listView);
                adapter.setUId(user.getUId());
                adapter.setBoard_title(board_id);
                int index = 0;

                ArrayList<ListViewItem> parent = new ArrayList<ListViewItem>();
                HashMap<Integer, ArrayList<ListViewItem>> citemList = new HashMap<Integer, ArrayList<ListViewItem>>();
                //parent list와 child list를 연결할 hashmap 변수 선언
                HashMap<ListViewItem, ArrayList<ListViewItem>> childlist = new HashMap<ListViewItem, ArrayList<ListViewItem>>();
                for(DataSnapshot parentSnapshot : dataSnapshot.child(board_id + "/comment").getChildren()){
                    index++;
                    ArrayList<ListViewItem> child= new ArrayList<ListViewItem>();
                    String commentContent = parentSnapshot.child("comment/").getValue().toString();
                    String uid = parentSnapshot.child("uid/").getValue().toString();
                    ListViewItem pitem = new ListViewItem(ContextCompat.getDrawable(BoardActivity.this, R.drawable.icon_notice), commentContent, uid);
                    parent.add(pitem);
                    for(DataSnapshot childSnapshot : parentSnapshot.child("/ccomment").getChildren()){
                        String ccommentContent = childSnapshot.child("comment/").getValue().toString();
                        System.out.println("댓글은 " + commentContent + ", 대댓글은 " + ccommentContent);
                        String cuid = childSnapshot.child("uid/").getValue().toString();
                        ListViewItem citem = new ListViewItem(ContextCompat.getDrawable(BoardActivity.this, R.drawable.icon_notice), ccommentContent, cuid);
                        child.add(citem);
                    }
                    citemList.put(index-1, child);
                    if(!child.isEmpty()){
                        childlist.put(parent.get(index-1), citemList.get(index-1));
                        System.out.println(parent.get(index-1).getBoard_title() + "에 맵핑 완료!!!");
                    }
                }
                adapter.setmParentList(parent);
                adapter.setmChildHashMap(childlist);
                listView.setAdapter(adapter);

                int groupCount = (int)adapter.getGroupCount();
                System.out.println("groundCount" + groupCount);
                for(int i = 0; i < groupCount; i++){
                    listView.expandGroup(i);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Failed to read value.", error.toException());
            }
        });

    }


}
