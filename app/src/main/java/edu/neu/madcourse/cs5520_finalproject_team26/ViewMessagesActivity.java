package edu.neu.madcourse.cs5520_finalproject_team26;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import com.squareup.picasso.Picasso;

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

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#b89928"));
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#b89928"));

        actionBar.setBackgroundDrawable(colorDrawable);

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
        Dialog dialog;
        dialog = new Dialog(this);
        Context context = this;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_message_popup);
        dialog.show();
        Button ok = dialog.findViewById(R.id.btn_okay_vm);
        Button home = dialog.findViewById(R.id.btn_home);
        TextView sender = dialog.findViewById(R.id.tv_senderName);

        messageRecords.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    Message msg = ds.getValue(Message.class);
                    assert message != null;
//                    if(message.getReceiverId().equals(loggedInUser) && message.getMessageText().equals(msg.getMessageText())
//                    && message.getLocation().equals(msg.getLocation()) && msg.getSenderId().equals(message.getSenderId())){
//                        messageRecords.child(ds.getKey()).updateChildren()
//                    }
                }
                messageAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        sender.setText(getUserName(message.getSenderId()));
        MultiAutoCompleteTextView location = dialog.findViewById(R.id.mtv_location);
        location.setText(message.getLocation());
        MultiAutoCompleteTextView messageText = dialog.findViewById(R.id.mtv_messageText);
        messageText.setText(message.getMessageText());
        ImageView senderImage = dialog.findViewById(R.id.iv_senderPic);
        Picasso.get().load(getUserProfilePic(message.getSenderId())).into(senderImage);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewMessagesActivity.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    public String getUserName(String userId){
        for(User u : userArrayList){
            if(u.getUserId().equals(userId)){
                return u.getUsername();
            }
        }
        return "Invalid Recipient";
    }

    private String getUserProfilePic(String senderId) {
        for(User u : userArrayList){
            if(u.getUserId().equals(senderId)){
                Log.d("logging userprofile" ,u.getProfilePic() );
                return u.getProfilePic();
            }
        }
        return "";
    }
}
