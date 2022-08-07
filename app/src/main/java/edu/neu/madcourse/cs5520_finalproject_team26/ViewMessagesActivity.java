package edu.neu.madcourse.cs5520_finalproject_team26;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.neu.madcourse.cs5520_finalproject_team26.models.Message;
import edu.neu.madcourse.cs5520_finalproject_team26.models.User;

public class ViewMessagesActivity extends AppCompatActivity implements MessageAdapter.MessagesInterface {
    MessageAdapter messageAdapter;
    RecyclerView messageRecyclerView;
    DatabaseReference messageRecords;
    DatabaseReference userRecords;
    ArrayList<Message> messageArrayList;
    ArrayList<User> userArrayList;
    String loggedInUserId;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String loggedInUser = currentUser.getUid();

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
        messageAdapter = new MessageAdapter(this,this,messageArrayList,userArrayList);
        messageRecyclerView.setAdapter(messageAdapter);


        userRecords = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("users");
        userRecords.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if(ds.getKey().equals(loggedInUser)){
                        loggedInUser = user.getUserId() ;
                    }else{
                        userArrayList.add(user);
                    }
                }
                messageRecords.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageArrayList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            Message message = ds.getValue(Message.class);
                            assert message != null;
                            if(message.getReceiverId().equals(loggedInUser)){
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

    @Override
    public void onItemClicked(Message message) {
        Toast.makeText(this, message.getMessageText(),Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ShowMessageActivity.class);
        intent.putExtra("senderName",message.getSenderId());
        intent.putExtra("Message",message.getMessageText());
        intent.putExtra("MsgLoc",message.getLocation());
        startActivity(intent);
    }
}
