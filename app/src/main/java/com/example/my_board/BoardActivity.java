package com.example.my_board;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
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

import java.util.ArrayList;
import java.util.HashMap;

public class BoardActivity extends AppCompatActivity {
    public ExListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        final ExpandableListView listView = (ExpandableListView) findViewById(R.id.listview);

        final FirebaseDatabase[] database = {FirebaseDatabase.getInstance()};
        DatabaseReference myRef = database[0].getReference("Content/");

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

        Intent intent = getIntent();
        final String board_id = intent.getStringExtra("board_id");

        final String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        final int[] commentCount = {0};


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


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listView.setAdapter((BaseExpandableListAdapter)null);
                User user = (User)getApplication();
                adapter = new ExListViewAdapter(null, null);
                adapter.setUId(user.getUId());
                adapter.setBoard_title(board_id);
                int index = 0;

                ArrayList<ListViewItem> parent = new ArrayList<ListViewItem>();
                ArrayList<ListViewItem> child= new ArrayList<ListViewItem>();
                //parent list와 child list를 연결할 hashmap 변수 선언
                HashMap<ListViewItem, ArrayList<ListViewItem>> childlist = new HashMap<ListViewItem, ArrayList<ListViewItem>>();
                for(DataSnapshot parentSnapshot : dataSnapshot.child(board_id + "/comment").getChildren()){
                    if(index != 0){
                        index++;
                    }
                    String commentContent = parentSnapshot.child("comment/").getValue().toString();
                    System.out.println("댓글은 " + commentContent);
                    String uid = parentSnapshot.child("uid/").getValue().toString();
                    ListViewItem pitem = new ListViewItem(ContextCompat.getDrawable(BoardActivity.this, R.drawable.icon_notice), commentContent, uid);
                    parent.add(pitem);
                    for(DataSnapshot childSnapshot : parentSnapshot.child("/ccomment").getChildren()){
                        String ccommentContent = childSnapshot.child("comment/").getValue().toString();
                        System.out.println("대댓글은 " + ccommentContent);
                        String cuid = childSnapshot.child("uid/").getValue().toString();
                        ListViewItem citem = new ListViewItem(ContextCompat.getDrawable(BoardActivity.this, R.drawable.icon_notice), ccommentContent, cuid);
                        child.add(citem);
                    }
                    if(!child.isEmpty()){
                        childlist.put(parent.get(index), child);
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


//                listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//                    @Override
//                    public void onGroupExpand(int groupPosition) {
//                    }
//                });
//                listView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//                    @Override
//                    public void onGroupCollapse(int groupPosition) {
//                    }
//                });
//                listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//                    @Override
//                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                        Toast.makeText(BoardActivity.this, "child_Clicked", Toast.LENGTH_LONG).show();
//                        return false;
//                    }
//                });
//
//                listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//                    @Override
//                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                        Toast.makeText(BoardActivity.this, "group_Clicked", Toast.LENGTH_LONG).show();
//                        return false;
//                    }
//                });
//                listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//                    @Override
//                    public void onGroupExpand(int groupPosition) {
//                        Toast.makeText(BoardActivity.this, "group open", Toast.LENGTH_LONG).show();
//                    }
//                });

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Failed to read value.", error.toException());
            }
        });

    }


}
