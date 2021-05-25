package com.example.my_board

import android.app.Application
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText


class Holder{
    var boardTitle: String? = null
    var editText: TextInputEditText? = null
    var editBtn: Button? = null
    var deleteBtn: Button? = null
    var position = 0
}