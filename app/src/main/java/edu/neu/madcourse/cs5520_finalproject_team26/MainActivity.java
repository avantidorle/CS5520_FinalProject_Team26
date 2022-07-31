package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import edu.neu.madcourse.cs5520_finalproject_team26.models.User;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference reference;

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        reference = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/")
//                .getReference("users");

//        String userId = UUID.randomUUID().toString();
//        User user = new User("dummyUser", userId);
//        ref.push().setValue(user);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user == null) {

            startActivity(new Intent(this, LoginActivity.class));
        }

    }
}