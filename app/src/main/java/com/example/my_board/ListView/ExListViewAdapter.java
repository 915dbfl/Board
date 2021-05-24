package com.example.my_board.ListView;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.my_board.Holder;
import com.example.my_board.ListViewItem;
import com.example.my_board.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ExListViewAdapter<ChildListViewHolder> extends BaseExpandableListAdapter {
    private ImageView board_icon;
    private TextView board_title;
    private TextView board_uid;
    private String uid;
    private String title;
    private ExpandableListView listView;
    private LinearLayout likePart;


    private ArrayList<ListViewItem> mParentList;
    private HashMap<ListViewItem, ArrayList<ListViewItem>> mChildHashMap;

    //ListViewAdapter의 생성자
    public ExListViewAdapter(ArrayList<ListViewItem> parentList, HashMap<ListViewItem, ArrayList<ListViewItem>> childHashMap, ExpandableListView listView){
        this.mParentList = parentList;
        this.mChildHashMap = childHashMap;
        this.listView = listView;
    }


    public void setmParentList(ArrayList<ListViewItem> parentList){
        this.mParentList = parentList;
    }

    public void setmChildHashMap(HashMap<ListViewItem, ArrayList<ListViewItem>> childHashMap){
        this.mChildHashMap = childHashMap;
    }



    public void setUId(String uid){
        this.uid = uid;
    }

    public void setBoard_title(String title){
        this.title = title;
    }


    @Override
    public int getGroupCount() {
        return mParentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(this.mChildHashMap.get(this.mParentList.get(groupPosition)) != null){
            return this.mChildHashMap.get(this.mParentList.get(groupPosition)).size();
        }
        else{
            return 0;
        }
    }

    @Override
    public ListViewItem getGroup(int groupPosition) {
        return mParentList.get(groupPosition);
    }

    @Override
    public ListViewItem getChild(int groupPosition, int childPosition) {
        return this.mChildHashMap.get(this.mParentList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
        final ListViewItem listViewItem = mParentList.get(groupPosition);
        System.out.println("로그인한 회원의 id는 " + uid + ", 게시판을 작성한 id는 " + listViewItem.getBoard_uid());
        Holder holder;
        System.out.println("가져온 부모 listView는 " +  listViewItem.getBoard_title());
        if(convertView == null){
            Context context = parent.getContext();
            holder = new Holder();
            holder.boardTitle = listViewItem.getBoard_title();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_mcitem, parent, false);
            holder.deleteBtn = (Button) convertView.findViewById(R.id.content_delete);
            if(!listViewItem.getBoard_uid().equals(uid)) {
                holder.deleteBtn.setVisibility(View.INVISIBLE);
            }
            holder.editBtn = (Button) convertView.findViewById(R.id.cComent);
            holder.editText = (TextInputEditText) convertView.findViewById(R.id.TextInputEditText_cComment);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
            if(!listViewItem.getBoard_uid().equals(uid)) {
                holder.deleteBtn.setVisibility(View.INVISIBLE);
            }else{
                holder.deleteBtn.setVisibility(View.VISIBLE);
            }
        }
        final Holder finalHolder = holder;
        holder.position = groupPosition;


        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("TAG", "새로운 값이 업데이트되었다.");
                mParentList.get(finalHolder.position).setBoard_cComment(s.toString());
            }
        });
        if(listViewItem.getBoard_uid().equals(uid)) {
            holder.deleteBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("삭제가 된 것은 " +  mParentList.get(finalHolder.position).getBoard_title());
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference removeContent = database.getReference("Content/" + title + "/comment/" + uid +  mParentList.get(finalHolder.position).getBoard_title() + "/");
                    removeContent.removeValue();
                }
            });
        }
        holder.editBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("클릭된 것의 제목은 " + mParentList.get(finalHolder.position).getBoard_title());
                String content = mParentList.get(finalHolder.position).getBoard_cComment();
                System.out.println("content 내용은 " + content);
                if(content == null || content.isEmpty()){
                    finalHolder.editText.setText("please,write!");
                }else{
                    System.out.println("대댓글은 " + content + ", 댓글은 " +  mParentList.get(finalHolder.position).getBoard_title());
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("comment", content);
                    hashMap.put("uid", uid);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("Content/" + title + "/comment/" + uid +  mParentList.get(finalHolder.position).getBoard_title() + "/");
                    reference.child("ccomment/" + uid + content + '/').setValue(hashMap);
                }
            }
        });


        //화면에 표시될 View(Layout이 infate된)으로부터 위젯에 대한 참조 획득
        board_icon = (ImageView) convertView.findViewById(R.id.board_img);
        board_title = (TextView) convertView.findViewById(R.id.board_title);
        board_uid = (TextView)convertView.findViewById(R.id.board_uid);

        //아이템 내 각 위젯에 데이터 반영
        board_icon.setImageDrawable(listViewItem.getBoard_icon());
        board_title.setText(listViewItem.getBoard_title());
        board_uid.setText(listViewItem.getBoard_uid());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ListViewItem listViewItem = getChild(groupPosition, childPosition);
        if(convertView == null){
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        //화면에 표시될 View(Layout이 infate된)으로부터 위젯에 대한 참조 획득
        board_icon = (ImageView) convertView.findViewById(R.id.board_img);
        board_title = (TextView) convertView.findViewById(R.id.board_title);
        board_uid = (TextView)convertView.findViewById(R.id.board_uid);
        likePart = (LinearLayout)convertView.findViewById(R.id.likePart);
        likePart.setVisibility(View.INVISIBLE);


        //아이템 내 각 위젯에 데이터 반영
        board_icon.setImageDrawable(listViewItem.getBoard_icon());
        board_title.setText(listViewItem.getBoard_title());
        System.out.println("제목, 내용 확인해보기" + listViewItem.getBoard_title() + ", " + listViewItem.getBoard_uid());
        board_uid.setText(listViewItem.getBoard_uid());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}