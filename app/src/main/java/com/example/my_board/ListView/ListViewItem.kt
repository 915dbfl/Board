package com.example.my_board.ListView

import android.graphics.drawable.Drawable


class ListViewItem(var board_icon: Drawable, var board_title: String, var board_uid: String) {
    var countLike = ""
        get() = field
        set(countLike) {
            field = countLike
        }
    var board_cComment: String? = null
    var board_content: String? = null

}
