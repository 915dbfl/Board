package com.example.my_board;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    private Drawable board_icon;
    private String board_title;

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
