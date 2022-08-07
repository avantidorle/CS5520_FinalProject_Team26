package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;

import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import edu.neu.madcourse.cs5520_finalproject_team26.models.User;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference reference;

    private FirebaseAuth auth;
    private Geocoder geocoder;
    private TextView locationDisplay;
    private double prevLatitudeValue;
    private double prevLongitudeValue;
    private double latitudeValue;
    private double longitudeValue;
    private List<Address> addresses;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private double distanceTravelled = 0.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy gfgPolicy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);
        }
        auth = FirebaseAuth.getInstance();
        locationDisplay = findViewById(R.id.locationDisplayHomeScreenId);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    99);

        } else {
            // already permission granted
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    try {
                        latitudeValue = location.getLatitude();
                        longitudeValue = location.getLongitude();
                        geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitudeValue, longitudeValue, 1);
                        String address = addresses.get(0).getAddressLine(0);
                        locationDisplay.setText(address);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,null);
                }
            });
        }



        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3*1000);


        locationCallback = new LocationCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        try {
                            prevLatitudeValue = latitudeValue;
                            prevLongitudeValue = longitudeValue;
                            latitudeValue = location.getLatitude();
                            longitudeValue = location.getLongitude();
                            geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            addresses = geocoder.getFromLocation(latitudeValue, longitudeValue, 1);
                            String address = addresses.get(0).getAddressLine(0);
                            locationDisplay.setText(address);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,null);

                    }
                }
            }
        };



    }

    @Override
    protected void onStart() {

        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if(user == null) {

            startActivity(new Intent(this, LoginActivity.class));
        }



    }


    private void locationDisplayHelper() {

    }

    public void logOutClick(View view) {

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void startGameClick(View view) {
    }

    public void addTriviaQuestionClick(View view) {
    }

    public void leaveAMessageClick(View view) {
    }

    public void leaderBoardClick(View view) {
    }

    public void viewMessagesClick(View view) {
    }

    public void profilePageClick(View view) {
    }
}