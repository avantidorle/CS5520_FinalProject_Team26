package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterUserActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText emailRegister;
    EditText passwordRegister;
    EditText nameRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        nameRegister = findViewById(R.id.displayNameInputId);
        emailRegister = findViewById(R.id.emailRegisterId);
        passwordRegister = findViewById(R.id.passwordRegisterId);

        auth = FirebaseAuth.getInstance();

    }

    public void registerUserSubmitClick(View view) {

        String email = emailRegister.getText().toString();
        String password = passwordRegister.getText().toString();
        String name = nameRegister.getText().toString();

        if(name.isEmpty()) {
            nameRegister.setError("Name is required");
            nameRegister.requestFocus();
            return;
        }

        if(email.isEmpty()) {
            emailRegister.setError("Email is required");
            emailRegister.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailRegister.setError("Please provide valid email!");
            emailRegister.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordRegister.setError("Password is required");
            passwordRegister.requestFocus();
            return;
        }

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                }
            }
        });





    }

    public void loginInRegisterClick(View view) {

        startActivity(new Intent(this, LoginActivity.class));
    }
}