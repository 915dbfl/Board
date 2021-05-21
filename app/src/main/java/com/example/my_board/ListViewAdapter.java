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
    private TextView countLike;
    private String uid;
    private String title;
    private int isboard;
    private int like;

    public String getLike() {
        return Integer.toString(like);
    }

    public void setLike(String like) {
        this.like = Integer.parseInt(like);
    }

    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

    public ListViewAdapter(){}

    @Override
    public int getCount(){
        return listViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final int pos = position;
        final Context context = parent.getContext();
        final ListViewItem listViewItem = listViewItemList.get(position);
        final View[] finalConvertView = {convertView};
        final ViewGroup finalParent = parent;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item,parent,false);
        }

        board_icon = (ImageView) convertView.findViewById(R.id.board_img);
        board_title = (TextView) convertView.findViewById(R.id.board_title);
        board_uid = (TextView)convertView.findViewById(R.id.board_uid);
        countLike = (TextView)convertView.findViewById(R.id.countLike);

        board_icon.setImageDrawable(listViewItem.getBoard_icon());
        board_title.setText(listViewItem.getBoard_title());
        board_uid.setText(listViewItem.getBoard_uid());
        countLike.setText(listViewItem.getCountLike());

        return convertView;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public Object getItem(int position){
        return listViewItemList.get(position);
    }

    public void addItem(Drawable icon, String title, String content, String uid, String countLike){
        ListViewItem item = new ListViewItem(icon, title, uid);
        item.setBoard_content(content);
        item.setCountLike(countLike);
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

