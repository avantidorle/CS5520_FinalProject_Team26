package edu.neu.madcourse.cs5520_finalproject_team26;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.neu.madcourse.cs5520_finalproject_team26.models.Message;
import edu.neu.madcourse.cs5520_finalproject_team26.models.User;

public class LeaveNoteActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener{

    SearchView receiver;
    EditText messageText;
    EditText location;
    Button leaveNote;

    ListView recipientNames;
    RecepientNamesAdapter rmAdapter;
    DatabaseReference userRecords;
    DatabaseReference messageRecords;
    ArrayList<User> users;
    String loggedInUser = "bd820e82-256a-4011-8fc9-6e3f92a3aaee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_note);

        receiver = findViewById(R.id.sv_recipient_lan);
        recipientNames = findViewById(R.id.lv_recipientNames_lan);
        location = findViewById(R.id.edt_location_lan);
        messageText = findViewById(R.id.et_note_lan);
        leaveNote = findViewById(R.id.btn_submit_lan);
        users = new ArrayList<>();
        rmAdapter = new RecepientNamesAdapter(this,users);
        messageRecords = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("messages");
        userRecords = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("users");
        userRecords.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)  {
                for(DataSnapshot ds: snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    users.add(user);
                }
                recipientNames.setAdapter(rmAdapter);
                rmAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        receiver.setOnQueryTextListener(this);
        recipientNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("logging onItem click", rmAdapter.getItem(position) );
                receiver.setQuery(rmAdapter.getItem(position),true);
                recipientNames.setVisibility(View.GONE);;
            }
        });
        leaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message newMsg = new Message();
                newMsg.setReceiverId(getUserId(receiver.getQuery().toString()));
                newMsg.setLocation(String.valueOf(location.getText()));
                newMsg.setSeen(false);
                newMsg.setMessageText(String.valueOf(messageText.getText()));
                newMsg.setSenderId(loggedInUser);
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
                newMsg.setSentTime(timeStamp);
                String key = messageRecords.push().getKey();
                messageRecords.child(key).setValue(newMsg);
                Toast.makeText(LeaveNoteActivity.this, "data added", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        recipientNames.setVisibility(View.VISIBLE);;
        rmAdapter.filter(text, new ArrayList<>(users));
        return false;
    }


    public String getUserId(String userName){
        for(User u : users){
            if(u.getUsername().equals(userName)){
                return u.getUserId();
            }
        }

        return "Invalid Recipient";
    }
}