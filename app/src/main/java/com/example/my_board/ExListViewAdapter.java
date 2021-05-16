package com.example.my_board;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExListViewAdapter<ChildListViewHolder> extends BaseExpandableListAdapter {
    private ImageView board_icon;
    private TextView board_title;
    private TextView board_uid;
    private TextInputEditText cComment;
    private Button content_delete;
    private Button comments;
    private String uid;
    private String title;

    private ArrayList<ListViewItem> mParentList;
    private HashMap<ListViewItem, ArrayList<ListViewItem>> mChildHashMap;

    //ListViewAdapter의 생성자
    public ExListViewAdapter(ArrayList<ListViewItem> parentList, HashMap<ListViewItem, ArrayList<ListViewItem>> childHashMap){
        this.mParentList = parentList;
        this.mChildHashMap = childHashMap;
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ListViewItem listViewItem = null;
        listViewItem = mParentList.get(groupPosition);
        if(convertView == null){
            Context context = parent.getContext();
            if(listViewItem.getBoard_uid().equals(uid)){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_mcitem,parent,false);
                content_delete = (Button) convertView.findViewById(R.id.content_delete);
                comments = (Button) convertView.findViewById(R.id.cComent);
                cComment = (TextInputEditText) convertView.findViewById(R.id.TextInputEditText_cComment);
                comments.setTag(listViewItem);
                cComment.setTag(listViewItem);
                content_delete.setTag(listViewItem);
                System.out.println("저장되는 listviewItem의 tagname은 " + listViewItem.getBoard_title());
                cComment.addTextChangedListener(new MyWatcher(cComment));
                content_delete.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference removeContent = database.getReference("Content/" + title + "/comment/" + uid+((ListViewItem)content_delete.getTag()).getBoard_title() + "/");
                        removeContent.removeValue();
                    }
                });

            }else{
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_citem,parent,false);
            }
        }
        comments.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                ListViewItem listView = (ListViewItem)comments.getTag();
                System.out.println("클릭된 것의 제목은 " + listView.getBoard_title());
                String content = listView.getBoard_cComment();
                System.out.println("content 내용은 " + content);
                if(content == null || content.isEmpty()){
                    cComment.setText("please,write!");
                }else{
                    System.out.println("대댓글은 " + content + ", 댓글은 " + listView.getBoard_title());
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("comment", content);
                    hashMap.put("uid", uid);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("Content/" + title + "/comment/" + uid+listView.getBoard_title() + "/");
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
    ///////////////////삭제 시, 사라지지 않음, 대댓글 작성 오류 및 뜨지 않음

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

