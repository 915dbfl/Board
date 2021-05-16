package com.example.my_board;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapter extends BaseAdapter {
    private ImageView board_icon;
    private TextView board_title;
    private TextView board_uid;
    private TextView board_content;
    private TextInputEditText cComment;
    private Button content_delete;
    private Button comments;
    private String uid;
    private String title;
    private int isboard;


    //Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

    //ListViewAdapter의 생성자
    public ListViewAdapter(){}

    @Override
    //Adapter에 사용되는 데이터의 개수를 리턴(필수 구현)
    public int getCount(){
        return listViewItemList.size();
    }

    @Override
    //position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴(필수 구현)
    public View getView(int position, View convertView, ViewGroup parent){
        final int pos = position;
        final Context context = parent.getContext();
        final ListViewItem listViewItem = listViewItemList.get(position);
        final View[] finalConvertView = {convertView};
        final ViewGroup finalParent = parent;

        //"listview_item" layout을 inflate하여 convertView 참조 획득
        if(convertView == null){
            if(isboard == 1){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item,parent,false);
            }
            else {
                if(listViewItem.getBoard_uid().equals(uid)){
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.listview_mcitem,parent,false);
                    content_delete = (Button) convertView.findViewById(R.id.content_delete);

                    content_delete.setOnClickListener(new Button.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference removeContent = database.getReference("Content/" + title + "/comment/" + uid+listViewItem.getBoard_title() + "/");
                            removeContent.removeValue();
                        }
                    });

                    comments = (Button) convertView.findViewById(R.id.cComent);
                    cComment = (TextInputEditText) convertView.findViewById(R.id.TextInputEditText_cComment);

                    comments.setOnClickListener(new Button.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            String ccomment = cComment.getText().toString();
                            if(ccomment.isEmpty()){
                                cComment.setText("please, write!");
                            }else{
                                System.out.println("대댓글은 " + ccomment);
                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("comment", ccomment);
                                hashMap.put("uid", uid);
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Content/" + title + "/comment/" + uid+listViewItem.getBoard_title() + "/");
                                reference.child("ccoment/" + uid + ccomment + '/').setValue(hashMap);
                            }
                        }
                    });
                }else{
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.listview_citem,parent,false);
                }
            }
            
//            리스트뷰 수정해야해!!!! 대댓글까지 달릴 수 있게!!!! 오키??????????????
        }

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

    //지정한 위치(position)에 있는 데이터와 관련 아이템(row)의 id를 리턴(필수 구현)
    @Override
    public long getItemId(int position){
        return position;
    }

    //지정한 위치(position)에 있는 데이터 리턴(필수 구현)
    @Override
    public Object getItem(int position){
        return listViewItemList.get(position);
    }

    //아이템 데이터 추가를 위한 함수.
    public void addItem(Drawable icon, String title, String content, String uid){
        ListViewItem item = new ListViewItem(icon, title, uid);
        item.setBoard_content(content);
        listViewItemList.add(item);

    }

    public void clear() {
        listViewItemList.clear();
    }

    public void setUId(String uid){
        this.uid = uid;
    }

    public void setBoard_title(String title){
        this.title = title;
    }

    public void setIsboard(int board){
        this.isboard = board;
    }


}

