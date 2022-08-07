package edu.neu.madcourse.cs5520_finalproject_team26;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ShowMessageActivity extends AppCompatActivity {

    ImageView senderImage;
    TextView senderName;
    MultiAutoCompleteTextView location;
    MultiAutoCompleteTextView msgText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_message);
        senderImage = findViewById(R.id.iv_senderImage);
        senderName = findViewById(R.id.tv_senderName_Sm);
        location = findViewById(R.id.mtv_loc);
        msgText = findViewById(R.id.mtv_msg_sm);

        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/mad-finalproject-team26.appspot.com/o/defaultProfileImage.jpg?alt=media&token=0943abc0-afed-4614-8c0e-7937f2dcb6ff").into(senderImage);
        senderName.setText(getIntent().getStringExtra("senderName"));
        location.setText(getIntent().getStringExtra("MsgLoc"));
        msgText.setText(getIntent().getStringExtra("Message"));


    }
}