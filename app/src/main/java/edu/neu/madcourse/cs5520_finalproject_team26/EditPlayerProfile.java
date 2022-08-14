package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;

public class EditPlayerProfile extends AppCompatActivity {
    String loggedInUserUserId = "";
    String presentUserKey = "";
    DatabaseReference usersTable;
    EditText playerUserName;
    ImageView playerProfileImageView;
    Button saveProfileDetails;
    ProgressBar progressBar;
    Uri imageURI;
    StorageReference reference = FirebaseStorage.getInstance().getReference();
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_player_profile);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#b89928"));
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#b89928"));

        actionBar.setBackgroundDrawable(colorDrawable);

        playerUserName = findViewById(R.id.playerUserName);
        playerProfileImageView = findViewById(R.id.playerProfileImageView);
        saveProfileDetails = findViewById(R.id.saveProfileDetails);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        loggedInUserUserId = getIntent().getStringExtra("loggedInUserUserId");
        usersTable = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("users");
        Query presentUser = usersTable.orderByChild("userId").equalTo(getIntent().getStringExtra("loggedInUserUserId"));
        presentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                    for (DataSnapshot usr: userSnapshot.getChildren()) {
                        String name = usr.child("username").getValue().toString();
                        String profilePic = usr.child("profilePic").getValue().toString();
                        presentUserKey = usr.getKey();
                        playerUserName.setText(name.toUpperCase());
                        Picasso.get().load(profilePic).into(playerProfileImageView);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        playerProfileImageView.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, 2);
        });


        saveProfileDetails.setOnClickListener(v -> {
            intent = new Intent(this, MainActivity.class);
            String playerName = playerUserName.getText().toString();
            if (!playerName.equals("") && imageURI == null) {
                usersTable.child(presentUserKey).child("username").setValue(playerName);
                intent.putExtra("loggedInUserUserId", loggedInUserUserId);
                startActivity(intent);
            }
            if (imageURI != null) {
                uploadToFirebase(imageURI);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageURI = data.getData();
            playerProfileImageView.setImageURI(imageURI);
        }
    }

    private void uploadToFirebase(Uri uri) {
        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        usersTable = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/").getReference("users");
                        usersTable.child(presentUserKey).child("profilePic").setValue(uri.toString());
                        progressBar.setVisibility(View.INVISIBLE);
                        String playerName = playerUserName.getText().toString();
                        if (!playerName.equals("")) {
                            usersTable.child(presentUserKey).child("username").setValue(playerName);
                            intent.putExtra("loggedInUserUserId", loggedInUserUserId);
                            startActivity(intent);
                        }
                        intent.putExtra("loggedInUserUserId", loggedInUserUserId);
                        startActivity(intent);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getBaseContext(), "Image upload failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}