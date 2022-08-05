package edu.neu.madcourse.cs5520_finalproject_team26;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.neu.madcourse.cs5520_finalproject_team26.models.Message;
import edu.neu.madcourse.cs5520_finalproject_team26.models.User;

public class ViewMessagesActivity extends AppCompatActivity {
    MessageAdapter messageAdapter;
    RecyclerView messageRecyclerView;
    DatabaseReference messageRecords;
    DatabaseReference userRecords;
    ArrayList<Message> messageArrayList;
    ArrayList<User> userArrayList;
    String loggedInUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);

        messageRecords = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("messages");;
        messageRecyclerView = findViewById(R.id.rv_messages_ms);
        messageRecyclerView.setHasFixedSize(true);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageArrayList  = new ArrayList<>();
        userArrayList  = new ArrayList<>();
        messageAdapter = new MessageAdapter(this,messageArrayList,userArrayList);
        messageRecyclerView.setAdapter(messageAdapter);
        //TODO : get loggedInuser from Intent;
        loggedInUserId = "e7328c89-392c-43c7-9bc8-fa07dc0cf833";

        userRecords = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("users");
        userRecords.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    userArrayList.add(user);
                }

                messageRecords.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            Message message = ds.getValue(Message.class);
                            assert message != null;
                            if(message.getReceiverId().equals(loggedInUserId)){
                                messageArrayList.add(message);
                            }
                        }
                        messageAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Log.d("logging users count", String.valueOf(userArrayList.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }



        });
    }
}
