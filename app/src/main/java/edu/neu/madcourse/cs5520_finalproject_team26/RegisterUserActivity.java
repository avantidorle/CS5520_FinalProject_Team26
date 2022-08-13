package edu.neu.madcourse.cs5520_finalproject_team26;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;
import java.util.UUID;

import edu.neu.madcourse.cs5520_finalproject_team26.models.User;

public class RegisterUserActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText emailRegister;
    EditText passwordRegister;
    EditText nameRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#b89928"));
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#b89928"));

        actionBar.setBackgroundDrawable(colorDrawable);


        nameRegister = findViewById(R.id.displayNameInputId);
        emailRegister = findViewById(R.id.emailRegisterId);
        passwordRegister = findViewById(R.id.passwordRegisterId);

        auth = FirebaseAuth.getInstance();

    }

    public void registerUserSubmitClick(View view) {

        String emailId = emailRegister.getText().toString();
        String password = passwordRegister.getText().toString();
        String username = nameRegister.getText().toString();

        if(username.isEmpty()) {
            nameRegister.setError("Name is required");
            nameRegister.requestFocus();
            return;
        }

        if(emailId.isEmpty()) {
            emailRegister.setError("Email is required");
            emailRegister.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            emailRegister.setError("Please provide valid email!");
            emailRegister.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordRegister.setError("Password is required");
            passwordRegister.requestFocus();
            return;
        }

        if(password.length() < 6) {
            passwordRegister.setError("Password should be atleast 6 characters");
            passwordRegister.requestFocus();
            return;
        }

        auth.createUserWithEmailAndPassword(emailId,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    String userId = UUID.randomUUID().toString();
                    User user = new User(userId,username,emailId);

                    FirebaseDatabase.getInstance().getReference("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(RegisterUserActivity.this, "Registered Successfully!",Toast.LENGTH_LONG).show();
                                        retrieveAndStoreToken();
                                        startActivity(new Intent(RegisterUserActivity.this, LoginActivity.class));

                                    }
                                    else {
                                        Toast.makeText(RegisterUserActivity.this, "Registration Failed!" + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                        Log.v("Registration failed", task.getException().getMessage());

                                    }
                                }
                            });


                }
                else {
                    Toast.makeText(RegisterUserActivity.this, "Registration Failed!" + task.getException().getMessage() ,Toast.LENGTH_LONG).show();
                    Log.v("Registration failed", task.getException().getMessage());

                }
            }
        });





    }

    public void loginInRegisterClick(View view) {

        startActivity(new Intent(this, LoginActivity.class));
    }

    private void retrieveAndStoreToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("logging fcm", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        FirebaseDatabase.getInstance().getReference("token").child(userID).setValue(token);

                    }
                });
    }
}