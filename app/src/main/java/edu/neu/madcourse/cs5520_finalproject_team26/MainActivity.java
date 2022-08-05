package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import edu.neu.madcourse.cs5520_finalproject_team26.models.User;

public class  MainActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private Button viewMessages;
    private Button leaveNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        
        
        
        
        reference = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/")
                .getReference("users");

        viewMessages = findViewById(R.id.btn_viewMessages);
        viewMessages.setOnClickListener(v -> openViewMessagesActivity());
        leaveNote = findViewById(R.id.btn_LeaveNote);
        leaveNote.setOnClickListener(v -> openLeaveNoteActivity());
        
//        String userId = UUID.randomUUID().toString();
//        User user = new User("dummyUser", userId);
//        ref.push().setValue(user);
    }

    private void openLeaveNoteActivity() {
        Intent intent = new Intent(this, LeaveNoteActivity.class);
        startActivity(intent);
    }

    private void openViewMessagesActivity() {
        Intent intent = new Intent(this, ViewMessagesActivity.class);
        startActivity(intent);
    }
}