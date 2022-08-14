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

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText emailLogin;
    EditText passwordLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#b89928"));
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#b89928"));

        actionBar.setBackgroundDrawable(colorDrawable);

        emailLogin = findViewById(R.id.emailLoginId);
        passwordLogin = findViewById(R.id.passwordLoginId);

        auth = FirebaseAuth.getInstance();
    }

    public void registerInLoginScreenClick(View view) {
        startActivity(new Intent(this, RegisterUserActivity.class));
    }

    public void loginButtonClick(View view) {

        String emailId = emailLogin.getText().toString();
        String password = passwordLogin.getText().toString();

        if(emailId.isEmpty()) {
            emailLogin.setError("Email is required");
            emailLogin.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            emailLogin.setError("Please provide valid email!");
            emailLogin.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordLogin.setError("Password is required");
            passwordLogin.requestFocus();
            return;
        }

//        if(password.length() < 6) {
//            passwordRegister.setError("Password should be atleast 6 characters");
//            passwordRegister.requestFocus();
//            return;
//        }

        auth.signInWithEmailAndPassword(emailId, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    retrieveAndStoreToken();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else {
                    Toast.makeText(LoginActivity.this, "Email or password is invalid! ",Toast.LENGTH_LONG).show();

                    Log.v("Login failed!", task.getException().getMessage());
                }
            }
        });
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