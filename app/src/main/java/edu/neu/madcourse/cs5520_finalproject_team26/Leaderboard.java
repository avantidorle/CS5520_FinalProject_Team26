package edu.neu.madcourse.cs5520_finalproject_team26;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.StringNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import edu.neu.madcourse.cs5520_finalproject_team26.models.User;

class UserData {
    String userId;
    String userName;
    int geoCoins;
    UserData(String userId, String userName, int geoCoins) {
        this.geoCoins = geoCoins;
        this.userId = userId;
        this.userName = userName;
    }
}


public class Leaderboard  extends AppCompatActivity implements View.OnClickListener  {

    private final String USERID = "bf611ba3-2f9c-4889-896f-1d1b465ee817";

    private ImageView firstPositionAvatar;
    private ImageView secondPositionAvatar;
    private ImageView thirdPositionAvatar;
    private TextView firstPositionText;
    private TextView secondPositionText;
    private TextView thirdPositionText;

    private TextView rank4;
    private TextView rank5;
    private TextView rank6;
    private TextView rank7;
    private TextView rank8;

    private TextView geocoins4;
    private TextView geocoins5;
    private TextView geocoins6;
    private TextView geocoins7;
    private TextView geocoins8;


    private DatabaseReference dbReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_leaderboard);
        firstPositionText = findViewById(R.id.first_position_text);
        secondPositionText = findViewById(R.id.second_position_text);
        thirdPositionText = findViewById(R.id.third_position_text);
        rank4 = findViewById(R.id.rank4);
        rank5 = findViewById(R.id.rank5);
        rank6 = findViewById(R.id.rank6);
        rank7 = findViewById(R.id.rank7);
        rank8 = findViewById(R.id.rank8);

        geocoins4 = findViewById(R.id.geocoin4);
        geocoins5 = findViewById(R.id.geocoin5);
        geocoins6 = findViewById(R.id.geocoin6);
        geocoins7 = findViewById(R.id.geocoin7);
        geocoins8 = findViewById(R.id.geocoin8);

        firstPositionAvatar = findViewById(R.id.first_position_avatar);
        secondPositionAvatar = findViewById(R.id.second_position_avatar);
        thirdPositionAvatar = findViewById(R.id.third_position_avatar);

        showOtherRanks();
    }

    private void showOtherRanks() {
        List<UserData> list = new ArrayList();

        dbReference = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/")
                .getReference("users");
        Query query = dbReference.orderByChild("geoCoins");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot snap: snapshot.getChildren()) {
                        UserData data = new UserData(snap.child("userId").getValue().toString(),
                                snap.child("username").getValue().toString(),
                                Integer.parseInt(snap.child("geoCoins").getValue().toString()));
                        list.add(data);
                    }

                    Collections.reverse(list);

                    createRankList(list);
                }
            }

            @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
            private void createRankList(List<UserData> list) {

                int currentRank = -1;

                for(int i = 0; i < list.size(); i++) {
                    if(USERID.equals(list.get(i).userId)) {
                        currentRank = i;
                        break;
                    }
                }
                updateFirstFive(list);

                if(currentRank > 7) {

                    if(currentRank < list.size()-1) {
                       updateLaterRankLast(list, currentRank);
                        currentRank = 6;
                    } else {
                        updateLaterRank(list, currentRank);
                        currentRank = 7;
                    }

                } else {
                    updateTopRank(list);
                }

               updateStyleForRank(currentRank);
            }

            private void updateStyleForRank(int currentRank) {
                switch (currentRank) {
                    case 3: rank4.setBackgroundColor(Color.parseColor("#704CAF50"));
                        geocoins4.setBackgroundColor(Color.parseColor("#704CAF50"));
                        break;
                    case 4: rank5.setBackgroundColor(Color.parseColor("#704CAF50"));
                        geocoins5.setBackgroundColor(Color.parseColor("#704CAF50"));
                        break;
                    case 5: rank6.setBackgroundColor(Color.parseColor("#704CAF50"));
                        geocoins6.setBackgroundColor(Color.parseColor("#704CAF50"));
                        break;
                    case 6: rank7.setBackgroundColor(Color.parseColor("#704CAF50"));
                        geocoins7.setBackgroundColor(Color.parseColor("#704CAF50"));
                        break;
                    case 7: rank8.setBackgroundColor(Color.parseColor("#704CAF50"));
                        geocoins8.setBackgroundColor(Color.parseColor("#704CAF50"));
                        break;
                }
            }

            @SuppressLint("SetTextI18n")
            private void updateTopRank(List<UserData> list) {
                rank6.setText("6 : "+list.get(5).userName);
                geocoins6.setText(String.valueOf(list.get(5).geoCoins));

                rank7.setText("7 : "+list.get(6).userName);
                geocoins7.setText(String.valueOf(list.get(6).geoCoins));

                rank8.setText("8 : "+list.get(7).userName);
                geocoins8.setText(String.valueOf(list.get(7).geoCoins));
            }

            @SuppressLint("SetTextI18n")
            private void updateLaterRank(List<UserData> list, int currentRank) {
                rank6.setText("6 : "+list.get(5).userName);
                geocoins6.setText(String.valueOf(list.get(5).geoCoins));

                rank7.setText(currentRank+" : "+list.get(currentRank-1).userName);
                geocoins7.setText(String.valueOf(list.get(currentRank-1).geoCoins));

                rank8.setText(currentRank+1+" : "+list.get(currentRank).userName);
                geocoins8.setText(String.valueOf(list.get(currentRank).geoCoins));
            }

            @SuppressLint("SetTextI18n")
            private void updateLaterRankLast(List<UserData> list, int currentRank) {
                rank6.setText(currentRank+" : "+list.get(currentRank-1).userName);
                geocoins6.setText(String.valueOf(list.get(currentRank-1).geoCoins));

                rank7.setText(currentRank+1+" : "+list.get(currentRank).userName);
                geocoins7.setText(String.valueOf(list.get(currentRank).geoCoins));

                rank8.setText(currentRank+2+" : "+list.get(currentRank+1).userName);
                geocoins8.setText(String.valueOf(list.get(currentRank+1).geoCoins));

            }

            @SuppressLint("SetTextI18n")
            private void updateFirstFive(List<UserData> list) {

                firstPositionText.setText(list.get(0).userName);
                secondPositionText.setText(list.get(1).userName);
                thirdPositionText.setText(list.get(2).userName);

                rank4.setText("4 : "+list.get(3).userName);
                geocoins4.setText(String.valueOf(list.get(3).geoCoins));

                rank5.setText("5 : "+list.get(4).userName);
                geocoins5.setText(String.valueOf(list.get(4).geoCoins));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
