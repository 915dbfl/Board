package com.example.my_board;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        TextView TextInputEditText_id;
        TextView TextInputEditText_password;
        Button Button_login;

        TextInputEditText_id = (TextView) findViewById(R.id.TextInputEditText_id);
        TextInputEditText_password = (TextView) findViewById(R.id.TextInputEditText_password);
        Button_login = (Button) findViewById(R.id.Button_login);

        Button_login.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


}
