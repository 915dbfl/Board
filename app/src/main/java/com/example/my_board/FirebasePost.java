package com.example.my_board;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class FirebasePost {
    public String id;
    public String pw;

    public FirebasePost(){

    }

    public FirebasePost(String id, String pw){
        this.id = id;
        this.pw = pw;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("pw", pw);
        return result;
    }

    public void postFirebaseDatabase(boolean add){
        //mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        if(add){
            FirebasePost post = new FirebasePost(id, pw);
            postValues = post.toMap();
        }
        childUpdates.put("/id_list/" + id, postValues);
        //mPostRerence.updateChildren(childUpdates);
    }
}
