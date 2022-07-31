package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class TriviaPageActivity extends AppCompatActivity {
    TextView currentPlayerLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    Geocoder geocoder;
    List<Address> addresses;
    double prevLatitude;
    double prevLongitude;
    double presentLatitude;
    double presentLongitude;
    double distanceTravelled = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_page);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy gfgPolicy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);
        }

        currentPlayerLocation = findViewById(R.id.location_trivia_page);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


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
                        currentPlayerLocation.setText(address);
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

                                geocoder = new Geocoder(TriviaPageActivity.this, Locale.getDefault());
                                addresses = geocoder.getFromLocation(presentLatitude, presentLongitude,1);
                                float[] distance = new float[1];
                                Location.distanceBetween(prevLatitude, prevLongitude, presentLatitude, presentLongitude,distance);
                                distanceTravelled = distanceTravelled + distance[0];
                                if (distanceTravelled > 150) {
                                    String address = addresses.get(0).getAddressLine(0);
                                    currentPlayerLocation.setText(address);
                                    distanceTravelled = 0;
                                    prevLatitude = location.getLatitude();
                                    prevLongitude = location.getLongitude();
                                    presentLatitude = location.getLatitude();
                                    presentLongitude = location.getLongitude();
                                }
//                                String address = addresses.get(0).getAddressLine(0) + distanceTravelled;
//                                currentPlayerLocation.setText(address);
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
}