package edu.neu.madcourse.cs5520_finalproject_team26;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.neu.madcourse.cs5520_finalproject_team26.models.User;

public class LeaveNoteActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener{

    SearchView receiver;
    EditText messageText;
    EditText location;
    Button leaveNote;

    ListView recepientNames;
    RecepientNamesAdapter rmAdapter;
    DatabaseReference userRecords;
    ArrayList<User> users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_note);

        receiver = findViewById(R.id.sv_recipient_lan);
        recepientNames = findViewById(R.id.lv_recipientNames_lan);
        location = findViewById(R.id.edt_location_lan);
        messageText = findViewById(R.id.et_note_lan);
        leaveNote = findViewById(R.id.btn_LeaveNote);
        users = new ArrayList<>();
        rmAdapter = new RecepientNamesAdapter(this,users);


        userRecords = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("users");
        userRecords.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    users.add(user);
                }
                recepientNames.setAdapter(rmAdapter);
                rmAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        receiver.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        rmAdapter.filter(text);
        return false;
    }
}