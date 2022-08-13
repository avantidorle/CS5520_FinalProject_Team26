package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;

import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import edu.neu.madcourse.cs5520_finalproject_team26.models.Question;
import edu.neu.madcourse.cs5520_finalproject_team26.models.User;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private FirebaseAuth auth;
    private Geocoder geocoder;
    private TextView locationDisplay;
    private double prevLatitude;
    private double prevLongitude;
    private double presentLatitude;
    private double presentLongitude;
    private List<Address> addresses;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private String address;
    private double distanceTravelled = 0.0f;
    private DatabaseReference locationReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy gfgPolicy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);
        }

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#b89928"));
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#b89928"));

        actionBar.setBackgroundDrawable(colorDrawable);

        auth = FirebaseAuth.getInstance();
        locationDisplay = findViewById(R.id.locationDisplayHomeScreenId);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    99);

        } else {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {

                if (location != null) {
                    try {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        prevLatitude = location.getLatitude();
                        prevLongitude = location.getLongitude();
                        presentLatitude = location.getLatitude();
                        presentLongitude = location.getLongitude();
                        geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        String address = addresses.get(0).getAddressLine(0);
                        locationDisplay.setText(address);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            });
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1000);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {


                            try {
                                prevLatitude = presentLatitude;
                                prevLongitude = presentLongitude;
                                presentLatitude = location.getLatitude();
                                presentLongitude = location.getLongitude();

                                geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                addresses = geocoder.getFromLocation(presentLatitude, presentLongitude,1);
                                float[] distance = new float[1];
                                Location.distanceBetween(prevLatitude, prevLongitude, presentLatitude, presentLongitude,distance);
                                distanceTravelled = distanceTravelled + distance[0];
                                if (distanceTravelled > 150) {

                                    String address = addresses.get(0).getAddressLine(0);
                                    locationDisplay.setText(address);
                                    distanceTravelled = 0;
                                    prevLatitude = location.getLatitude();
                                    prevLongitude = location.getLongitude();
                                    presentLatitude = location.getLatitude();
                                    presentLongitude = location.getLongitude();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            };
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if(user == null) {

            startActivity(new Intent(this, LoginActivity.class));
        }



    }

    public void logOutClick(View view) {

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void startGameClick(View view) {
        Intent intent = new Intent(this, TriviaPageActivity.class);
        intent.putExtra("address",address);
        FirebaseUser user = auth.getInstance().getCurrentUser();
        String loggedInUserID = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("users");
        locationReference = FirebaseDatabase.getInstance().getReference("locations");

        Query questionAvailable = locationReference.orderByChild("location").equalTo(address);
        questionAvailable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot questionAvailableSnapshot) {
                if (questionAvailableSnapshot.exists()) {
                    reference.child(loggedInUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User userDetails = snapshot.getValue(User.class);
                            if (userDetails != null) {
                                String userId = userDetails.getUserId();
                                intent.putExtra("loggedInUserID", userId);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.v("Passing user id in intent", "Couldn't pass user id in add trivia question page");

                        }
                    });
                } else {
                    showPopUp();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addTriviaQuestionClick(View view) {
        Intent intent =  new Intent(this, AddQuestion.class);
        intent.putExtra("address",address);
        FirebaseUser user = auth.getInstance().getCurrentUser();
        String loggedInUserID = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child(loggedInUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userDetails = snapshot.getValue(User.class);
                if (userDetails != null) {
                    String userId = userDetails.getUserId();
                    intent.putExtra("loggedInUserID", userId);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.v("Passing user id in intent", "Couldn't pass user id in add trivia question page");

            }
        });


    }

    public void leaveAMessageClick(View view) {
        Intent intent = new Intent(this, LeaveNoteActivity.class);
        startActivity(intent);
    }

    public void leaderBoardClick(View view) {
        Intent intent = new Intent(this, Leaderboard.class);
        startActivity(intent);
    }

    public void viewMessagesClick(View view) {
        Intent intent = new Intent(this, ViewMessagesActivity.class);
        startActivity(intent);
    }

    public void profilePageClick(View view) {
        Intent intent = new Intent(this, PlayerSummaryActivity.class);
        FirebaseUser user = auth.getInstance().getCurrentUser();
        String loggedInUserID = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child(loggedInUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userDetails = snapshot.getValue(User.class);
                if (userDetails != null) {
                    String userId = userDetails.getUserId();
                    intent.putExtra("loggedInUserID", userId);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.v("Passing user id in intent", "Couldn't pass user id in add trivia question page");

            }
        });
    }

    public void showPopUp() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_no_available_questions);
        dialog.show();
    }
}