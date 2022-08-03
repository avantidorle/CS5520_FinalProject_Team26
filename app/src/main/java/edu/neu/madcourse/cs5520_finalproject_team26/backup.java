package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.annotation.NonNull;
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
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class backup extends AppCompatActivity {
    private TextView currentPlayerLocation;
    private TextView geoCoins;
    private TextView question;
    private RadioGroup radioGroup;
    private RadioButton option1;
    private RadioButton option2;
    private RadioButton option3;
    private RadioButton option4;

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
    private DatabaseReference usersTable;
    private DatabaseReference locationTable;
    private DatabaseReference questionsTable;
    private DatabaseReference questionUserTable;
    private String loggedInUserUserId = "dab90150-4740-4e88-ac66-50bf608a9655";
    String k = "";
    boolean qExists = false;
    boolean questionAlreadyInQuestionUserTable = false;
    boolean isQuestionSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_page);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy gfgPolicy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);
        }


//        radioGroup = findViewById(R.id.radioGroup);
//        String value =
//                ((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId()))
//                        .getText().toString();
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                Toast.makeText(getBaseContext(), value, Toast.LENGTH_SHORT).show();
//            }
//        });

        // CONTINUOUS LOCATION UPDATES

        currentPlayerLocation = findViewById(R.id.location_trivia_page);
        /*
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
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            };
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
         */

        currentPlayerLocation.setText("1209 Boylston, Boston, MA");

        setQuestion();
//        Toast.makeText(this,answer, Toast.LENGTH_SHORT).show();

        // USER SPECIFIC DETAILS
        usersTable =  FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("users");

        /* Get count of geocoins collected by user */
        geoCoins = findViewById(R.id.playerGeoCoins);
        Query loggedInUser = usersTable.orderByChild("userId").equalTo(loggedInUserUserId);
        loggedInUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        String totalCoins = ds.child("geoCoins").getValue().toString();
                        geoCoins.setText(totalCoins);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        // GET QUESTIONS BASED ON LOCATION OF THE USER
//        locationTable =  FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("locations");
//        questionsTable = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("questions");
//        question = findViewById(R.id.question);
//        Query locationBasedQuestionsIds = locationTable.orderByChild("location").equalTo(currentPlayerLocation.getText().toString());
//        locationBasedQuestionsIds.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()) {
//                    for (DataSnapshot ds: snapshot.getChildren()) {
//                        for (DataSnapshot qid: ds.child("questions").getChildren()) {
//                            if (qid.exists()) {
//                                String questionId = qid.getValue().toString();
//
//                                questionsTable.orderByChild("questionId").equalTo(questionId).addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot questionSnapshot) {
//                                        if (questionSnapshot.exists()) {
//                                            for(DataSnapshot questionObj: questionSnapshot.getChildren()) {
//                                                String questionText = questionObj.child("questionText").getValue().toString();
//                                                k = k + questionText;
//                                                question.setText(questionText);
//                                                option1 = findViewById(R.id.option1);
//                                                option2 = findViewById(R.id.option2);
//                                                option3 = findViewById(R.id.option3);
//                                                option4 = findViewById(R.id.option4);
//                                                int o=0;
//                                                for (DataSnapshot options: questionObj.child("options").getChildren()) {
//                                                    if (o == 0) {
//                                                        option1.setText(options.getValue().toString());
//                                                    } else if (o == 1) {
//                                                        option2.setText(options.getValue().toString());
//                                                    } else if (o == 2) {
//                                                        option3.setText(options.getValue().toString());
//                                                    } else if (o == 3) {
//                                                        option4.setText(options.getValue().toString());
//                                                    }
//                                                    o++;
//                                                }
//                                                break;
//                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//
//
//
//                            }
//                            break;
//                        }
//
//                    }
//                }
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


    }

    public void setQuestion() {
        qExists = false;
        questionAlreadyInQuestionUserTable = false;
        isQuestionSet = false;
        locationTable =  FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("locations");
        questionsTable = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("questions");
        questionUserTable = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("questionuser");
        question = findViewById(R.id.question);
        Query locationBasedQuestionsIds = locationTable.orderByChild("location").equalTo(currentPlayerLocation.getText().toString());
        locationBasedQuestionsIds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        for (DataSnapshot qid: ds.child("questions").getChildren()) {
                            if (qid.exists()) {
                                String questionId = qid.getValue().toString();

                                Query questionAnsweredByPlayer = questionUserTable.orderByChild("userId").equalTo(loggedInUserUserId);
                                questionAnsweredByPlayer.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot questionAnsweredByPlayerSnapshot) {
                                        if (questionAnsweredByPlayerSnapshot.exists()) {
                                            for (DataSnapshot qABPS: questionAnsweredByPlayerSnapshot.getChildren()) {
                                                String quesAnswered = qABPS.child("questionId").getValue().toString();
                                                if (questionId.equals(quesAnswered)) {
                                                    questionAlreadyInQuestionUserTable = true;
                                                    String playerAnsweredCorrectly = qABPS.child("answer").getValue().toString();
                                                    if (playerAnsweredCorrectly.equals("false")) {
                                                        // set the question

                                                        questionsTable.orderByChild("questionId").equalTo(questionId).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot questionSnapshot) {
                                                                if (questionSnapshot.exists()) {
                                                                    for(DataSnapshot questionObj: questionSnapshot.getChildren()) {
                                                                        String questionText = questionObj.child("questionText").getValue().toString();
                                                                        isQuestionSet = true;
                                                                        k = k + questionText;
                                                                        question.setText(questionText);
                                                                        option1 = findViewById(R.id.option1);
                                                                        option2 = findViewById(R.id.option2);
                                                                        option3 = findViewById(R.id.option3);
                                                                        option4 = findViewById(R.id.option4);
                                                                        int o=0;
                                                                        for (DataSnapshot options: questionObj.child("options").getChildren()) {
                                                                            if (o == 0) {
                                                                                option1.setText(options.getValue().toString());
                                                                            } else if (o == 1) {
                                                                                option2.setText(options.getValue().toString());
                                                                            } else if (o == 2) {
                                                                                option3.setText(options.getValue().toString());
                                                                            } else if (o == 3) {
                                                                                option4.setText(options.getValue().toString());
                                                                            }
                                                                            o++;
                                                                        }
                                                                        break;
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });


                                                    }
                                                }

                                                if (isQuestionSet) {
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                if (!questionAlreadyInQuestionUserTable) {
                                    // set the question

                                    questionsTable.orderByChild("questionId").equalTo(questionId).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot questionSnapshot) {
                                            if (questionSnapshot.exists()) {
                                                for(DataSnapshot questionObj: questionSnapshot.getChildren()) {
                                                    String questionText = questionObj.child("questionText").getValue().toString();
                                                    isQuestionSet = true;
                                                    k = k + questionText;
                                                    question.setText(questionText);
                                                    option1 = findViewById(R.id.option1);
                                                    option2 = findViewById(R.id.option2);
                                                    option3 = findViewById(R.id.option3);
                                                    option4 = findViewById(R.id.option4);
                                                    int o=0;
                                                    for (DataSnapshot options: questionObj.child("options").getChildren()) {
                                                        if (o == 0) {
                                                            option1.setText(options.getValue().toString());
                                                        } else if (o == 1) {
                                                            option2.setText(options.getValue().toString());
                                                        } else if (o == 2) {
                                                            option3.setText(options.getValue().toString());
                                                        } else if (o == 3) {
                                                            option4.setText(options.getValue().toString());
                                                        }
                                                        o++;
                                                    }
                                                    break;
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
                        if (isQuestionSet) {
                            break;
                        }
                        if (isQuestionSet) {
                            break;
                        }
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void selectedOption1(View view) {
        Toast.makeText(getBaseContext(), "1", Toast.LENGTH_SHORT).show();
    }

    public void selectedOption2(View view) {
        Toast.makeText(getBaseContext(), "2", Toast.LENGTH_SHORT).show();
    }

    public void selectedOption3(View view) {
        Toast.makeText(getBaseContext(), "3", Toast.LENGTH_SHORT).show();
    }

    public void selectedOption4(View view) {
        Toast.makeText(getBaseContext(), "4", Toast.LENGTH_SHORT).show();
    }

    public void goToNextQuestion(View view) {
        Toast.makeText(getBaseContext(), "Next Question", Toast.LENGTH_SHORT).show();
        setQuestion();
    }
}