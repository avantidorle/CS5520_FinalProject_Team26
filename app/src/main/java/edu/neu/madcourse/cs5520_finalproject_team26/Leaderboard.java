package edu.neu.madcourse.cs5520_finalproject_team26;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import edu.neu.madcourse.cs5520_finalproject_team26.models.User;


public class Leaderboard  extends AppCompatActivity implements View.OnClickListener  {

    private String USERID = "";

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
    private DatabaseReference userDatabaseReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#b89928"));
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#b89928"));

        actionBar.setBackgroundDrawable(colorDrawable);
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

        getLoggedinUser();
        showOtherRanks();
    }


    private void getLoggedinUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userDatabaseReference = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/")
                    .getReference("users").child(user.getUid());
            userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    USERID = Objects.requireNonNull(snapshot.child("userId").getValue()).toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void showOtherRanks() {
        List<User> list = new ArrayList();

        dbReference = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/")
                .getReference("users");
        Query query = dbReference.orderByChild("geoCoins");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot snap: snapshot.getChildren()) {
                        User data = snap.getValue(User.class);
                        list.add(data);
                    }

                    Collections.reverse(list);

                    createRankList(list);
                }
            }

            @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
            private void createRankList(List<User> list) {

                int currentRank = -1;

                for(int i = 0; i < list.size(); i++) {
                    if(USERID.equals(list.get(i).getUserId())) {
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
            private void updateTopRank(List<User> list) {
                rank6.setText("6 : "+list.get(5).getUsername());
                geocoins6.setText(String.valueOf(list.get(5).getGeoCoins()));

                rank7.setText("7 : "+list.get(6).getUsername());
                geocoins7.setText(String.valueOf(list.get(6).getGeoCoins()));

                rank8.setText("8 : "+list.get(7).getUsername());
                geocoins8.setText(String.valueOf(list.get(7).getGeoCoins()));
            }

            @SuppressLint("SetTextI18n")
            private void updateLaterRank(List<User> list, int currentRank) {
                rank6.setText("6 : "+list.get(5).getUsername());
                geocoins6.setText(String.valueOf(list.get(5).getGeoCoins()));

                rank7.setText(currentRank+" : "+list.get(currentRank-1).getUsername());
                geocoins7.setText(String.valueOf(list.get(currentRank-1).getGeoCoins()));

                rank8.setText(currentRank+1+" : "+list.get(currentRank).getUsername());
                geocoins8.setText(String.valueOf(list.get(currentRank).getGeoCoins()));
            }

            @SuppressLint("SetTextI18n")
            private void updateLaterRankLast(List<User> list, int currentRank) {
                rank6.setText(currentRank+" : "+list.get(currentRank-1).getUsername());
                geocoins6.setText(String.valueOf(list.get(currentRank-1).getGeoCoins()));

                rank7.setText(currentRank+1+" : "+list.get(currentRank).getUsername());
                geocoins7.setText(String.valueOf(list.get(currentRank).getGeoCoins()));

                rank8.setText(currentRank+2+" : "+list.get(currentRank+1).getUsername());
                geocoins8.setText(String.valueOf(list.get(currentRank+1).getGeoCoins()));

            }

            @SuppressLint("SetTextI18n")
            private void updateFirstFive(List<User> list) {

                firstPositionText.setText(list.get(0).getUsername());
                Picasso.get().load(list.get(0).getProfilePic())
                        .into(firstPositionAvatar);

                secondPositionText.setText(list.get(1).getUsername());
                Picasso.get().load(list.get(1).getProfilePic())
                        .into(secondPositionAvatar);

                thirdPositionText.setText(list.get(2).getUsername());
                Picasso.get().load(list.get(2).getProfilePic())
                        .into(thirdPositionAvatar);

                rank4.setText("4 : "+list.get(3).getUsername());
                geocoins4.setText(String.valueOf(list.get(3).getGeoCoins()));

                rank5.setText("5 : "+list.get(4).getUsername());
                geocoins5.setText(String.valueOf(list.get(4).getGeoCoins()));
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
