package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlayerSummaryActivity extends AppCompatActivity {
    private TextView playerName;
    private TextView totalGeoCoins;
    private TextView totalQuestionsContributedCount;
    private TextView totalQuestionsAnsweredCount;
    private TextView playerRank;
    private ImageView playerImage;
    private TextView visitedLocations;
    private TextView editProfile;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String usrId = user.getUid();
    private String loggedInUserUserId = "";

    DatabaseReference usersTable;
    DatabaseReference questionsTable;
    DatabaseReference questionUserTable;
    ArrayList<String> locations = new ArrayList<>();
    int rank = 0;
    String locationsVisited = "";
    int correctlyAnsweredQuestionsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_summary);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#b89928"));
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#b89928"));

        actionBar.setBackgroundDrawable(colorDrawable);

        loggedInUserUserId = getIntent().getStringExtra("loggedInUserID");

        // display player name, total geocoins, total questions contributed
        playerName = findViewById(R.id.playerName);
        totalGeoCoins = findViewById(R.id.totalGeoCoins);
        totalQuestionsContributedCount = findViewById(R.id.totalQuestionsContributedCount);
        playerImage = findViewById(R.id.playerProfileImageView);
        editProfile = findViewById(R.id.editProfile);
        usersTable = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("users");
        questionsTable = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("questions");
        Query presentUser = usersTable.orderByChild("userId").equalTo(loggedInUserUserId);
        presentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                    for (DataSnapshot usr: userSnapshot.getChildren()) {
                        String name = usr.child("username").getValue().toString();
                        String geoCoins =  usr.child("geoCoins").getValue().toString();
                        String profilePic = usr.child("profilePic").getValue().toString();
                        playerName.setText(name.toUpperCase());
                        totalGeoCoins.setText(geoCoins);
                        Picasso.get().load(profilePic).into(playerImage);


                        Query questionsContributed = questionsTable.orderByChild("createdBy").equalTo(loggedInUserUserId);
                        questionsContributed.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot questionsContributedSnapshot) {
                                if (questionsContributedSnapshot.exists()){
                                    totalQuestionsContributedCount.setText(String.valueOf(questionsContributedSnapshot.getChildrenCount()));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // display total questions answered
        questionUserTable = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("questionuser");
        totalQuestionsAnsweredCount = findViewById(R.id.totalQuestionsAnsweredCount);
        Query questionsAnswered = questionUserTable.orderByChild("userId").equalTo(loggedInUserUserId);
        questionsAnswered.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot questionsAnsweredSnapshot) {
                if (questionsAnsweredSnapshot.exists()) {
                    for (DataSnapshot QA: questionsAnsweredSnapshot.getChildren()) {
                        String answer = QA.child("answer").getValue().toString();
                        if (answer == "true") {
                            correctlyAnsweredQuestionsCount++;
                            totalQuestionsAnsweredCount.setText(String.valueOf(correctlyAnsweredQuestionsCount));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // display rank of player
        Query orderPlayersByScore = usersTable.orderByChild("geoCoins");
        playerRank = findViewById(R.id.playerRank);
        orderPlayersByScore.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot sp: snapshot.getChildren()) {
                        String userId = sp.child("userId").getValue().toString();
                        rank = rank + 1;
                        if (userId.equals(loggedInUserUserId)) {
                            break;
                        }
                    }

                    playerRank.setText(String.valueOf( (snapshot.getChildrenCount()) - rank + 1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // display question covered by the player
        visitedLocations = findViewById(R.id.visitedLocations);
        Query userSpecificRecords = questionUserTable.orderByChild("userId").equalTo(loggedInUserUserId);
        userSpecificRecords.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ss: snapshot.getChildren()) {
                        String location = ss.child("location").getValue().toString();
                        if (!locations.contains(location)) {
                            locations.add(location);
                            locationsVisited = locationsVisited + location +  "\n";
                            visitedLocations.setText(locationsVisited);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditPlayerProfile.class);
            intent.putExtra("loggedInUserUserId", loggedInUserUserId);
            startActivity(intent);
        });
    }
}