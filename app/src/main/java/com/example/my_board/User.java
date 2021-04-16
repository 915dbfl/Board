package com.example.my_board;

import android.app.Application;

public class User extends Application {
    private String UId;
    private String Upwd;
    private String Email;
    @Override
    public void onCreate() { super.onCreate(); }


    public String getUId() {
        return UId;
    }

    public void setUId(String uid) {
        int num = uid.indexOf("@");
        UId = uid.substring(0, num);
    }

    public String getUpwd() {
        return Upwd;
    }

    public void setUpwd(String upwd) {
        Upwd = upwd;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

}
