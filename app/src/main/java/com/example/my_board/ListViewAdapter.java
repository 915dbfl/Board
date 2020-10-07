package com.example.my_board;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private ImageView board_icon;
    private TextView board_title;
    private TextView board_content;


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

        //"listview_item" layout을 inflate하여 convertView 참조 획득
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item,parent,false);
        }

        //화면에 표시될 View(Layout이 infate된)으로부터 위젯에 대한 참조 획득
        board_icon = (ImageView) convertView.findViewById(R.id.board_img);
        board_title = (TextView) convertView.findViewById(R.id.board_title);
        board_content = (TextView) convertView.findViewById(R.id.board_content);

        ListViewItem listViewItem = listViewItemList.get(position);

        //아이템 내 각 위젯에 데이터 반영
        board_icon.setImageDrawable(listViewItem.getBoard_icon());
        board_title.setText(listViewItem.getBoard_title());
        board_content.setText(listViewItem.getBoard_content());

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
    public void addItem(Drawable icon, String title, String content){
        ListViewItem item = new ListViewItem();

        item.setBoard_title(title);
        item.setBoard_content(content);
        item.setBoard_icon(icon);

        listViewItemList.add(item);
    }
}

