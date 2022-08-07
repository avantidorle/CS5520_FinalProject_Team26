package edu.neu.madcourse.cs5520_finalproject_team26;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.neu.madcourse.cs5520_finalproject_team26.models.Message;
import edu.neu.madcourse.cs5520_finalproject_team26.models.User;

public class LeaveNoteActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener{

    SearchView receiver;
    EditText messageText;
    EditText location_edt;
    Button leaveNote;

    ListView recipientNames;
    RecepientNamesAdapter rmAdapter;
    DatabaseReference userRecords;
    DatabaseReference messageRecords;
    ArrayList<User> users;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String loggedInUser = currentUser.getUid();

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Geocoder geocoder;
    private List<Address> addresses;
    private double prevLatitude;
    private double prevLongitude;
    private double presentLatitude;
    private double presentLongitude;
    private double distanceTravelled = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_note);

        receiver = findViewById(R.id.sv_recipient_lan);
        recipientNames = findViewById(R.id.lv_recipientNames_lan);
        location_edt = findViewById(R.id.edt_location_lan);
        messageText = findViewById(R.id.et_note_lan);
        leaveNote = findViewById(R.id.btn_submit_lan);
        users = new ArrayList<>();
        rmAdapter = new RecepientNamesAdapter(this,users);

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
                        location_edt.setText(address);
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

                                geocoder = new Geocoder(LeaveNoteActivity.this, Locale.getDefault());
                                addresses = geocoder.getFromLocation(presentLatitude, presentLongitude,1);
                                float[] distance = new float[1];
                                Location.distanceBetween(prevLatitude, prevLongitude, presentLatitude, presentLongitude,distance);
                                distanceTravelled = distanceTravelled + distance[0];
                                if (distanceTravelled > 150) {
                                    String address = addresses.get(0).getAddressLine(0);
                                    location_edt.setText(address);
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

        messageRecords = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("messages");
        userRecords = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("users");
        userRecords.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)  {
                for(DataSnapshot ds: snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if(ds.getKey().equals(loggedInUser)){
                        loggedInUser = user.getUserId() ;
                    }
                    users.add(user);
                }
                recipientNames.setAdapter(rmAdapter);
                rmAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        receiver.setOnQueryTextListener(this);
        recipientNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("logging onItem click", rmAdapter.getItem(position) );
                receiver.setQuery(rmAdapter.getItem(position),true);
                recipientNames.setVisibility(View.GONE);;
            }
        });
        leaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message newMsg = new Message();
                newMsg.setReceiverId(getUserId(receiver.getQuery().toString()));
                newMsg.setLocation(String.valueOf(location_edt.getText()));
                newMsg.setSeen(false);
                newMsg.setMessageText(String.valueOf(messageText.getText()));
                newMsg.setSenderId(loggedInUser);
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
                newMsg.setSentTime(timeStamp);
                String key = messageRecords.push().getKey();
                messageRecords.child(key).setValue(newMsg);
                Toast.makeText(LeaveNoteActivity.this, "Note was sent successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        recipientNames.setVisibility(View.VISIBLE);;
        rmAdapter.filter(text, new ArrayList<>(users));
        return false;
    }


    public String getUserId(String userName){
        for(User u : users){
            if(u.getUsername().equals(userName)){
                return u.getUserId();
            }
        }

        return "Invalid Recipient";
    }
}