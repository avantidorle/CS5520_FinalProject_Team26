package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        triviaGamePage = findViewById(R.id.trivia_game_page_button);
        playerSummaryPage = findViewById(R.id.player_summary_button);
        addDummyData = findViewById(R.id.addDummyData);

        triviaGamePage.setOnClickListener(v -> {
            Intent intent = new Intent(this, TriviaPageActivity.class);
            startActivity(intent);
        });

        playerSummaryPage.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayerSummaryActivity.class);
            startActivity(intent);
        });

        addDummyData.setOnClickListener(v -> {
            // add user
            reference = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/")
                    .getReference("questionuser");

//            String username = "poojanagude";
//            String userID = UUID.randomUUID().toString();
//            int geoCoins = 2;
//            String emailId = "poojanagude1998@gmail.com";
//            String password = "pooja";
//            User user = new User(username, userID, geoCoins, emailId, password);
//            reference.push().setValue(user);


//            String usrId = "dab90150-4740-4e88-ac66-50bf608a9655";
//            String qId = "1ccb5566-6dab-488f-b335-7dd3a83c8115";
//            boolean vote = false;
//            boolean answer = false;
//            QuestionUser questionUser = new QuestionUser(usrId, qId, vote, answer);
//            reference.push().setValue(questionUser);
        });

//        String userId = UUID.randomUUID().toString();
//        User user = new User("dummyUser", userId);
//        ref.push().setValue(user);
    }
}