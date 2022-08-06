package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import edu.neu.madcourse.cs5520_finalproject_team26.models.QuestionUser;
import edu.neu.madcourse.cs5520_finalproject_team26.models.User;

public class MainActivity extends AppCompatActivity {

    Button triviaGamePage;
    Button playerSummaryPage;
    Button addDummyData;

    private DatabaseReference reference;
    private FirebaseAuth auth;

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




        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user == null) {

            startActivity(new Intent(this, LoginActivity.class));
        }

    }

    public void logOutClick(View view) {


        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }
}