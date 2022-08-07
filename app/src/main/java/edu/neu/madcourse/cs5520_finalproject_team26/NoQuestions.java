package edu.neu.madcourse.cs5520_finalproject_team26;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class NoQuestions extends AppCompatActivity {
    ImageView goBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_questions);
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy gfgPolicy =
//                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(gfgPolicy);
//        }

//        goBackButton = findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(v -> {
            Toast.makeText(getBaseContext(), "exit", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

    }

    public void goToMainPage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}