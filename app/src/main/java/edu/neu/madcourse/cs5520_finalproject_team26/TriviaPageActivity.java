package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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

import edu.neu.madcourse.cs5520_finalproject_team26.models.QuestionUser;

public class TriviaPageActivity extends AppCompatActivity {
    private TextView currentPlayerLocation;
    private TextView geoCoins;
    private TextView question;
    private RadioGroup radioGroup;
    private RadioButton option1;
    private RadioButton option2;
    private RadioButton option3;
    private RadioButton option4;
    private TextView upVotes;
    private TextView downVotes;
    private Button nextQuestion;
    private ImageView likeIcon;
    private ImageView dislikeIcon;

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
    int randomNumber = 0;
    int questionIterator = 0;
    String presentQuestionId = "";
    String presentQuestionCreator = "";
    String presentQuestionAnswer = "0";
    String questionHint = "";
    String questionLikedDislikedStatus = "not voted";
    boolean questionAnsweredCorrectlyByPlayer = false;
    boolean questionUserPairAlreadyExists = false;
    int iterator = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_page);
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

        // CONTINUOUS LOCATION UPDATES
        currentPlayerLocation = findViewById(R.id.location_trivia_page);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        /*
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
        } */


        currentPlayerLocation.setText("Central Park");

        // add a record in the question user table only if it does nor exist
        locationTable =  FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("locations");
        questionUserTable = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("questionuser");
        Query locationBasedQuestionsIds = locationTable.orderByChild("location").equalTo(currentPlayerLocation.getText().toString());
        locationBasedQuestionsIds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        iterator = 0;
                        for (DataSnapshot qid: ds.child("questions").getChildren()) {
                            if (qid.exists()) {

                                String usrIdQuestionId = loggedInUserUserId + " " + qid.getValue().toString();
                                Query userIdQuestionIdPair = questionUserTable.orderByChild("userIdAndQuestionId").equalTo(usrIdQuestionId);
                                userIdQuestionIdPair.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot userIdQuestionIdPairSnapshot) {
                                        iterator++;
                                        if(!userIdQuestionIdPairSnapshot.exists()) {
                                            String usrId = loggedInUserUserId;
                                            String qId = qid.getValue().toString();
                                            String vote = "not voted";
                                            boolean ans = false;
                                            String usrIdQuesId = usrId + " " + qId;
                                            String usrIdNLocAns = usrId + " " + currentPlayerLocation.getText().toString() + " " + ans;
                                            String loc = currentPlayerLocation.getText().toString();
                                            QuestionUser questionUser = new QuestionUser(usrId, qId, vote, ans, usrIdQuesId, usrIdNLocAns, loc);
                                            questionUserTable.push().setValue(questionUser);
                                        }
                                        if (iterator == ds.child("questions").getChildrenCount()) {
                                            setQuestion();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        questionUserTable = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("questionuser");
        question = findViewById(R.id.question);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        radioGroup = findViewById(R.id.radioGroup);
        upVotes = findViewById(R.id.upvotesCount);
        downVotes = findViewById(R.id.downvotesCount);
        nextQuestion = findViewById(R.id.nextQuestion);
        likeIcon = findViewById(R.id.receivedMessage_pn);
        dislikeIcon = findViewById(R.id.sentMessage_pn);

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

//        setQuestion();

    }

    public void doesQuestionUserPairAlreadyExist(String loggedInUserUserId ,String presentQuestionId) {
        questionUserPairAlreadyExists = false;
        String usrQues = loggedInUserUserId + " " + presentQuestionId;
        Query questionUserPair = questionUserTable.orderByChild("userIdAndQuestionId").equalTo(usrQues);
        questionUserPair.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    questionUserPairAlreadyExists = true;
                } else {
                    questionUserPairAlreadyExists = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setQuestion() {
        radioGroup.clearCheck();
        likeIcon.setImageResource(R.mipmap.heart_unfilled_round);
        dislikeIcon.setImageResource(R.mipmap.dislike_icon_round);
        questionAnsweredCorrectlyByPlayer = false;
        locationTable =  FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("locations");
        questionsTable = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("questions");
        questionUserTable = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("questionuser");

        //
        Query questionAnsweredIncorrectly = questionUserTable.orderByChild("userIdAndLocationAndAnswer").equalTo(loggedInUserUserId + " " + currentPlayerLocation.getText().toString() + " "+ false);
        questionAnsweredIncorrectly.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    randomNumber = (int) (Math.random() * (snapshot.getChildrenCount()));
                    questionIterator = 0;
                    if (snapshot.getChildrenCount() > 0) {
                        for (DataSnapshot sp : snapshot.getChildren()) {
                            String questionId = sp.child("questionId").getValue().toString();
                            if (randomNumber == questionIterator) {
                                presentQuestionId = questionId;
                                doesQuestionUserPairAlreadyExist(loggedInUserUserId, presentQuestionId);
                                Query getQuestion = questionsTable.orderByChild("questionId").equalTo(presentQuestionId);
                                getQuestion.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot questionSnapshot) {
                                        if (questionSnapshot.exists()) {
                                            for (DataSnapshot qSnap : questionSnapshot.getChildren()) {
                                                String questionText = qSnap.child("questionText").getValue().toString();
                                                String likes = qSnap.child("upVotes").getValue().toString();
                                                String dislikes = qSnap.child("downVotes").getValue().toString();
                                                String createdBy = qSnap.child("createdBy").getValue().toString();
                                                presentQuestionCreator = createdBy;
                                                upVotes.setText(likes);
                                                downVotes.setText(dislikes);
                                                presentQuestionAnswer = qSnap.child("answer").getValue().toString();
                                                questionHint = qSnap.child("hint").getValue().toString();
                                                question.setText(presentQuestionAnswer + " " + questionText);
                                                int o = 0;
                                                option1.setEnabled(true);
                                                option2.setEnabled(true);
                                                option3.setEnabled(true);
                                                option4.setEnabled(true);
                                                option1.setBackgroundColor(Color.parseColor("#fff8e3"));
                                                option2.setBackgroundColor(Color.parseColor("#fff8e3"));
                                                option3.setBackgroundColor(Color.parseColor("#fff8e3"));
                                                option4.setBackgroundColor(Color.parseColor("#fff8e3"));
                                                nextQuestion.setEnabled(false);


                                                for (DataSnapshot options : qSnap.child("options").getChildren()) {
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
                                Query questionsAnsweredByUser = questionUserTable.orderByChild("userId").equalTo(loggedInUserUserId);
                                questionsAnsweredByUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot questionsAnsweredSnapshot) {
                                        if (questionsAnsweredSnapshot.exists()) {
                                            questionLikedDislikedStatus = "not voted";
                                            for (DataSnapshot qAS : questionsAnsweredSnapshot.getChildren()) {
                                                String quesId = qAS.child("questionId").getValue().toString();
                                                if (quesId.equals(presentQuestionId)) {
                                                    String vote = qAS.child("vote").getValue().toString();
                                                    if (vote.equals("liked") || vote.equals("disliked")) {
                                                        questionLikedDislikedStatus = vote;

                                                        likeIcon = findViewById(R.id.receivedMessage_pn);
                                                        if (vote.equals("liked")) {
                                                            likeIcon = findViewById(R.id.receivedMessage_pn);
                                                            likeIcon.setImageResource(R.mipmap.like_icon_round);
                                                        } else {
                                                            dislikeIcon = findViewById(R.id.sentMessage_pn);
                                                            dislikeIcon.setImageResource(R.mipmap.dislike_filled_round);
                                                        }
                                                    }
                                                    break;
                                                }
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                break;
                            }
                            questionIterator++;
                        }
                    }
                } else {
                    showPopUp();
                    Toast.makeText(getBaseContext(), "done with all questions", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showPopUp() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_no_questions);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void checkForRightAnswer(String selectedAnswer) {
        nextQuestion.setEnabled(true);
        questionAnsweredCorrectlyByPlayer = false;
        if (selectedAnswer.equals(presentQuestionAnswer)) {
            if (selectedAnswer.equals("0")) {
                option1.setBackgroundColor(Color.parseColor("#d6f78d"));
            } else if (selectedAnswer.equals("1")) {
                option2.setBackgroundColor(Color.parseColor("#d6f78d"));
            } else if (selectedAnswer.equals("2")) {
                option3.setBackgroundColor(Color.parseColor("#d6f78d"));
            } else if (selectedAnswer.equals("3")) {
                option4.setBackgroundColor(Color.parseColor("#d6f78d"));
            }
            questionAnsweredCorrectlyByPlayer = true;
            usersTable.orderByChild("userId").equalTo(loggedInUserUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                    if (userSnapshot.exists()) {
                        for (DataSnapshot user: userSnapshot.getChildren()) {
                            String key = user.getKey();
                            int currentScore = Integer.parseInt(user.child("geoCoins").getValue().toString());
                            int updatedScore = currentScore + 1;
                            usersTable.child(key).child("geoCoins").setValue(updatedScore);
                            geoCoins.setText(String.valueOf(updatedScore));
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            if (selectedAnswer.equals("0")) {
                option1.setBackgroundColor(Color.parseColor("#ffb9b3"));
            } else if (selectedAnswer.equals("1")) {
                option2.setBackgroundColor(Color.parseColor("#ffb9b3"));
            } else if (selectedAnswer.equals("2")) {
                option3.setBackgroundColor(Color.parseColor("#ffb9b3"));
            } else if (selectedAnswer.equals("3")) {
                option4.setBackgroundColor(Color.parseColor("#ffb9b3"));
            }
            questionAnsweredCorrectlyByPlayer = false;
        }

        String usrId = loggedInUserUserId;
        String quesId = presentQuestionId;
        String vote = questionLikedDislikedStatus;
        boolean answer = questionAnsweredCorrectlyByPlayer;
        String usrQuesId = usrId + " " + quesId;
        String usrIdNLocNAns = usrId + " " + currentPlayerLocation.getText().toString() + " " + answer;
        String loc = currentPlayerLocation.getText().toString();
        if (questionUserPairAlreadyExists) {
            questionUserTable.orderByChild("userIdAndQuestionId").equalTo(usrQuesId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            String key = ds.getKey();
                            questionUserTable.child(key).child("answer").setValue(questionAnsweredCorrectlyByPlayer);
                            questionUserTable.child(key).child("userIdAndLocationAndAnswer").setValue(usrIdNLocNAns);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            QuestionUser questionUser = new QuestionUser(usrId, quesId, vote, answer, usrQuesId, usrIdNLocNAns, loc);
            questionUserTable.push().setValue(questionUser);
            doesQuestionUserPairAlreadyExist(usrId, quesId);
        }

    }

    public void selectedOption1(View view) {
        checkForRightAnswer("0");
        option1.setEnabled(false);
        option2.setEnabled(false);
        option3.setEnabled(false);
        option4.setEnabled(false);
    }

    public void selectedOption2(View view) {
        checkForRightAnswer("1");
        option1.setEnabled(false);
        option2.setEnabled(false);
        option3.setEnabled(false);
        option4.setEnabled(false);
    }

    public void selectedOption3(View view) {
        checkForRightAnswer("2");
        option1.setEnabled(false);
        option2.setEnabled(false);
        option3.setEnabled(false);
        option4.setEnabled(false);
    }

    public void selectedOption4(View view) {
        checkForRightAnswer("3");
        option1.setEnabled(false);
        option2.setEnabled(false);
        option3.setEnabled(false);
        option4.setEnabled(false);
    }

    public void goToNextQuestion(View view) {
        nextQuestion = findViewById(R.id.nextQuestion);
        if (option1.isEnabled() && option2.isEnabled() && option3.isEnabled() && option4.isEnabled()) {
            Toast.makeText(getBaseContext(), "Select an answer to proceed to next question", Toast.LENGTH_LONG).show();
        } else {
            setQuestion();
        }

    }

    public void likeQuestion(View view) {
        doesQuestionUserPairAlreadyExist(loggedInUserUserId, presentQuestionId);
        if (questionLikedDislikedStatus.equals("not voted")) {
            questionsTable.orderByChild("questionId").equalTo(presentQuestionId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot questionSnapshot) {
                    if (questionSnapshot.exists()) {
                        for (DataSnapshot qSnap : questionSnapshot.getChildren()) {
                            String key = qSnap.getKey();
                            int currentLikesCount = Integer.parseInt(qSnap.child("upVotes").getValue().toString());
                            int newLikesCount = currentLikesCount + 1;
                            questionsTable.child(key).child("upVotes").setValue(newLikesCount);
                            upVotes.setText(String.valueOf(newLikesCount));
                            likeIcon = findViewById(R.id.receivedMessage_pn);
                            likeIcon.setImageResource(R.mipmap.like_icon_round);
                            String usrId = loggedInUserUserId;
                            String qId = presentQuestionId;
                            String vote = "liked";
                            questionLikedDislikedStatus = vote;
                            boolean answer = questionAnsweredCorrectlyByPlayer;
                            String usrQuesId = usrId + " " + qId;
                            String usrIdNLocNAns = usrId + " " + currentPlayerLocation.getText().toString() + " " + answer;
                            String loc = currentPlayerLocation.getText().toString();
                            if (questionUserPairAlreadyExists) {
                                questionUserTable.orderByChild("userIdAndQuestionId").equalTo(usrQuesId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot ds: snapshot.getChildren()) {
                                                String key = ds.getKey();
                                                questionUserTable.child(key).child("vote").setValue(vote);
                                                // update the geoPoints of the creator of the question which got a like
                                                Query questionCreator = usersTable.orderByChild("userId").equalTo(presentQuestionCreator);
                                                questionCreator.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot questionCreatorSnapshot) {
                                                        if (questionCreatorSnapshot.exists()) {
                                                            for (DataSnapshot creator: questionCreatorSnapshot.getChildren()) {
                                                                String k = creator.getKey();
                                                                int prevGeoCoins = Integer.parseInt(creator.child("geoCoins").getValue().toString());
                                                                int updatedGeoCoins = prevGeoCoins + 1;
                                                                usersTable.child(k).child("geoCoins").setValue(updatedGeoCoins);
                                                                break;
                                                            }
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
                                //
                            } else {
                                QuestionUser questionUser = new QuestionUser(usrId, qId, vote, answer, usrQuesId, usrIdNLocNAns, loc);
                                questionUserTable.push().setValue(questionUser);
                                // update the geoPoints of the creator of the question which got a like
                                Query questionCreator = usersTable.orderByChild("userId").equalTo(presentQuestionCreator);
                                questionCreator.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot questionCreatorSnapshot) {
                                        if (questionCreatorSnapshot.exists()) {
                                            for (DataSnapshot creator: questionCreatorSnapshot.getChildren()) {
                                                String k = creator.getKey();
                                                int prevGeoCoins = Integer.parseInt(creator.child("geoCoins").getValue().toString());
                                                int updatedGeoCoins = prevGeoCoins + 1;
                                                usersTable.child(k).child("geoCoins").setValue(updatedGeoCoins);
                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                doesQuestionUserPairAlreadyExist(loggedInUserUserId, presentQuestionId);
                            }
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Toast.makeText(getBaseContext(), "You already " + questionLikedDislikedStatus +" the question", Toast.LENGTH_LONG).show();
        }
    }

    public void dislikeQuestion(View view) {
        doesQuestionUserPairAlreadyExist(loggedInUserUserId, presentQuestionId);
        if (questionLikedDislikedStatus.equals("not voted")) {
            questionsTable.orderByChild("questionId").equalTo(presentQuestionId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot questionSnapshot) {
                    if (questionSnapshot.exists()) {
                        for (DataSnapshot qSnap : questionSnapshot.getChildren()) {
                            String key = qSnap.getKey();
                            int currentDislikesCount = Integer.parseInt(qSnap.child("downVotes").getValue().toString());
                            int newDisLikesCount = currentDislikesCount + 1;
                            questionsTable.child(key).child("downVotes").setValue(newDisLikesCount);
                            downVotes.setText(String.valueOf(newDisLikesCount));
                            dislikeIcon = findViewById(R.id.sentMessage_pn);
                            dislikeIcon.setImageResource(R.mipmap.dislike_filled_round);
                            String usrId = loggedInUserUserId;
                            String qId = presentQuestionId;
                            String vote = "disliked";
                            questionLikedDislikedStatus = vote;
                            boolean answer = questionAnsweredCorrectlyByPlayer;
                            String usrQuesId = usrId + " " + qId;
                            String usrIdNLocNAns = usrId + " " + currentPlayerLocation.getText().toString() + " " + answer;
                            String loc = currentPlayerLocation.getText().toString();
                            if (questionUserPairAlreadyExists) {
                                questionUserTable.orderByChild("userIdAndQuestionId").equalTo(usrQuesId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot ds: snapshot.getChildren()) {
                                                String key = ds.getKey();
                                                questionUserTable.child(key).child("vote").setValue(vote);
                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                //
                            } else {
                                QuestionUser questionUser = new QuestionUser(usrId, qId, vote, answer, usrQuesId, usrIdNLocNAns, loc);
                                questionUserTable.push().setValue(questionUser);
                                doesQuestionUserPairAlreadyExist(loggedInUserUserId, presentQuestionId);
                            }
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            Toast.makeText(getBaseContext(), "You already " + questionLikedDislikedStatus +" the question", Toast.LENGTH_LONG).show();
        }
    }

    public void getQuestionHint(View view) {
        Toast.makeText(getBaseContext(), questionHint, Toast.LENGTH_LONG).show();
    }
}