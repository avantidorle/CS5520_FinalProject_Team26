package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import edu.neu.madcourse.cs5520_finalproject_team26.models.User;

public class MainActivity extends AppCompatActivity {

    Button triviaGamePage;
    Button playerSummaryPage;

    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        triviaGamePage = findViewById(R.id.trivia_game_page_button);
        playerSummaryPage = findViewById(R.id.player_summary_button);

        triviaGamePage.setOnClickListener(v -> {
            Intent intent = new Intent(this, TriviaPageActivity.class);
            startActivity(intent);
        });

        playerSummaryPage.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayerSummaryActivity.class);
            startActivity(intent);
        });

        reference = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/")
                .getReference("users");

//        String userId = UUID.randomUUID().toString();
//        User user = new User("dummyUser", userId);
//        ref.push().setValue(user);
    }
}