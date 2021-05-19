package com.example.my_board;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    Button Button_register;
    TextView TextView_register_id;
    TextView TextView_register_pw;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        //액션 바 등록하기
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");

        //파이어베이스 접근 설정
        firebaseAuth = FirebaseAuth.getInstance();

        TextView_register_id = (TextView) findViewById(R.id.TextView_register_id);
        TextView_register_pw = (TextView) findViewById(R.id.TextView_register_pw);
        Button_register = (Button) findViewById(R.id.Button_register);


        //register버튼 리스너 달기
        Button_register.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                //회원 정보 가져오기
                if(TextUtils.isEmpty(TextView_register_id.getText()) || TextUtils.isEmpty(TextView_register_pw.getText())){
                    Toast.makeText(RegisterActivity.this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    final String email = TextView_register_id.getText().toString();
                    String pw = TextView_register_pw.getText().toString();

                    System.out.println(email + "," + pw);

                    firebaseAuth.createUserWithEmailAndPassword(email,pw).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //가입 성공 시
                            if(task.isSuccessful()){
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String uid = user.getUid();
                                String email = user.getEmail();

                                //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("uid", uid);
                                hashMap.put("email", email);

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(uid).setValue(hashMap);

                                //가입이 이루어졌을 시 가입 화면을 빠져나감.
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(RegisterActivity.this, "회원가입을 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(RegisterActivity.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
                }
            }
        });

    }


}
