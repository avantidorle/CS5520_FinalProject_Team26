package edu.neu.madcourse.cs5520_finalproject_team26;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
    Button ok;
    Button home;
    Dialog dialog;

    ListView recipientNames;
    RecepientNamesAdapter rmAdapter;
    DatabaseReference userRecords;
    DatabaseReference messageRecords;
    DatabaseReference tokenRecords;
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

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#b89928"));
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#b89928"));

        actionBar.setBackgroundDrawable(colorDrawable);


        receiver = findViewById(R.id.sv_recipient_lan);
        recipientNames = findViewById(R.id.lv_recipientNames_lan);
        location_edt = findViewById(R.id.edt_location_lan);
        messageText = findViewById(R.id.et_note_lan);
        leaveNote = findViewById(R.id.btn_submit_lan);
        users = new ArrayList<>();
        rmAdapter = new RecepientNamesAdapter(this,users);

        Context context = this;

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
                Log.d("logging RECEIVER", receiver.getQuery().toString());
                if(!receiver.getQuery().toString().equals("")){
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
                    dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.messagesent_popup);
                    dialog.show();
                    ok = dialog.findViewById(R.id.btn_okay);
                    home = dialog.findViewById(R.id.btn_backtoHome);

                    DatabaseReference tokenRecords = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("token");
                    final String[] registrationToken = {""};
                    final String[] receiverKey = {""};
                    userRecords.addValueEventListener(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot snapshot) {
                              for(DataSnapshot ds : snapshot.getChildren()){
                                  User user = ds.getValue(User.class);
                                  if(user.getUserId().equals(newMsg.getReceiverId())){
                                      receiverKey[0] = ds.getKey().toString();
                                      DatabaseReference tokenRecords =  FirebaseDatabase.getInstance().getReference().child("token");
                                      tokenRecords.addValueEventListener(new ValueEventListener() {
                                          @Override
                                          public void onDataChange(@NonNull DataSnapshot snapshot) {
                                              for(DataSnapshot ds : snapshot.getChildren()){
                                                  if(ds.getKey().equals(receiverKey[0])){
                                                      registrationToken[0] = ds.getValue().toString();
                                                      FcmNotificationsSender notification = new FcmNotificationsSender(registrationToken[0], "New message received ", "Location : " + newMsg.getLocation() + " \n Message : " + newMsg.getMessageText(), getApplicationContext(), LeaveNoteActivity.this);
                                                      notification.SendNotifications();
                                                  }
                                              }
                                          }

                                          @Override
                                          public void onCancelled(@NonNull DatabaseError error) {

                                          }
                                      });
                                  }
                              }
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError error) {

                          }


                      });

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, LeaveNoteActivity.class);
                            startActivity(intent);
                        }
                    });
                    home.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }else{
                    Toast.makeText(context, "Please enter recipient Id", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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