package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText emailLogin;
    EditText passwordLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else {
                    Toast.makeText(LoginActivity.this, "Email or password is invalid! ",Toast.LENGTH_LONG).show();

                    Log.v("Login failed!", task.getException().getMessage());
                }
            }
        });
    }
}