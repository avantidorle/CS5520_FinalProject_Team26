package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class CommonUtils {


    private String userNameTemp;

    public CommonUtils(){

    }


    public String getUserNameFromId(String userId){
        getUserNameFromIdHelper(userId);
        return userNameTemp;
    }

    public void getUserNameFromIdHelper(String userId){

    }
}
