package com.example.my_board.ListView

import android.app.Application
import android.graphics.drawable.BitmapDrawable

class ListViewItem(var board_title: String, var board_uid: String, var character : BitmapDrawable) : Application() {
    var countLike = ""
        get() = field
        set(countLike) {
            field = countLike
        }
    var board_cComment: String? = null
    var board_content: String? = null

}
