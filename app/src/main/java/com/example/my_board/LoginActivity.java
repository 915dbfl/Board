package com.example.my_board;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        firebaseAuth = FirebaseAuth.getInstance();

        final TextView TextInputEditText_id;
        final TextView TextInputEditText_password;
        Button Button_login;
        Button Button_join;

        TextInputEditText_id = (TextView) findViewById(R.id.TextInputEditText_id);
        TextInputEditText_password = (TextView) findViewById(R.id.TextInputEditText_password);
        Button_login = (Button) findViewById(R.id.Button_login);
        Button_join = (Button) findViewById(R.id.Button_register);

        final User user = (User)getApplicationContext();

        Button_login.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(TextInputEditText_id.getText()) || TextUtils.isEmpty(TextInputEditText_password.getText())){
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    String id = TextInputEditText_id.getText().toString().trim();
                    String pw = TextInputEditText_password.getText().toString().trim();
                    firebaseAuth.signInWithEmailAndPassword(id,pw).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser cuser = firebaseAuth.getCurrentUser();
                                user.setUId(cuser.getEmail());
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
                }
            }
        });

        Button_join.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }




}
