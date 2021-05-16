package com.example.my_board;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MyWatcher implements TextWatcher {
    private TextInputEditText edit;
    private ListViewItem item;
    public MyWatcher(TextInputEditText edit) {
        this.edit = edit;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.item = (ListViewItem) edit.getTag();
        System.out.println("textedit이 존재하는 list의 제목은 " + this.item.getBoard_title());
        if (item != null) {
            Log.d("TAG", "새로운 값이 업데이트되었다.");
            item.setBoard_cComment(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}