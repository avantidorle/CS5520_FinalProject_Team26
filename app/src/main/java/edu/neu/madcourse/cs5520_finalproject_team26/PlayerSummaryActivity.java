package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Locale;

public class PlayerSummaryActivity extends AppCompatActivity {
    private TextView playerName;
    private TextView totalGeoCoins;
    private TextView totalQuestionsContributedCount;
    private TextView totalQuestionsAnsweredCount;
    private TextView playerRank;
    private ImageView playerImage;
    private String loggedInUserUserId = "dab90150-4740-4e88-ac66-50bf608a9655";

    DatabaseReference usersTable;
    DatabaseReference questionsTable;
    DatabaseReference questionUserTable;
    int rank = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_summary);

        // display player name, total geocoins, total questions contributed
        playerName = findViewById(R.id.playerName);
        totalGeoCoins = findViewById(R.id.totalGeoCoins);
        totalQuestionsContributedCount = findViewById(R.id.totalQuestionsContributedCount);
        playerImage = findViewById(R.id.playerImage);
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


                        Query questionsContributed = questionsTable.orderByChild("createdBy").equalTo(name);
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
                    totalQuestionsAnsweredCount.setText(String.valueOf(questionsAnsweredSnapshot.getChildrenCount()));
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
    }
}