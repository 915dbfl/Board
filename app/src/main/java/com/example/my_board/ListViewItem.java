package com.example.my_board;

import android.graphics.drawable.Drawable;
import android.view.View;

public class ListViewItem {
    private Drawable board_icon;
    private String board_title;
    private String board_uid;
    private int countLike;

    public ListViewItem(Drawable icon, String title, String id){
        board_icon = icon;
        board_title = title;
        board_uid = id;
    }
    public String getCountLike() { return Integer.toString(countLike); }

    public void setCountLike(String countLike) { this.countLike = Integer.parseInt(countLike); }

    public String getBoard_cComment() {
        return board_cComment;
    }

    public void setBoard_cComment(String board_cComment) {
        this.board_cComment = board_cComment;
    }

    private String board_cComment;

    public String getBoard_content() {
        return board_content;
    }

    public void setBoard_content(String board_content) {
        this.board_content = board_content;
    }

    private String board_content;

    public String getBoard_uid() {
        return board_uid;
    }

    public void setBoard_uid(String board_uid) {
        this.board_uid = board_uid;
    }

    public Drawable getBoard_icon() {
        return board_icon;
    }

    public void setBoard_icon(Drawable board_icon) {
        this.board_icon = board_icon;
    }

    public String getBoard_title() {
        return board_title;
    }

    public void setBoard_title(String board_title) {
        this.board_title = board_title;
    }

}
