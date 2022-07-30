package edu.neu.madcourse.cs5520_finalproject_team26;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.neu.madcourse.cs5520_finalproject_team26.models.Location;
import edu.neu.madcourse.cs5520_finalproject_team26.models.Question;

public class AddQuestion extends AppCompatActivity implements View.OnClickListener {

    private static final String CREATED_BY = "testUser";

    private Button addQuestion;
    private EditText questionText;
    private EditText optionA;
    private EditText optionB;
    private EditText optionC;
    private EditText optionD;
    private EditText hint;
    private Spinner answer;
    private DatabaseReference databaseReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions);

        questionText = findViewById(R.id.question_text);
        optionA = findViewById(R.id.option_a_text);
        optionB = findViewById(R.id.option_b_text);
        optionC = findViewById(R.id.option_c_text);
        optionD = findViewById(R.id.option_d_text);
        hint = findViewById(R.id.hint_text);
        answer = findViewById(R.id.options_spinner);
        addQuestion = findViewById(R.id.add_question_button);

        addQuestion.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        databaseReference = FirebaseDatabase.getInstance("https://mad-finalproject-team26-default-rtdb.firebaseio.com/")
                .getReference("questions");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> options = new ArrayList<>(Arrays.asList(
                        optionA.getText().toString(),
                        optionB.getText().toString(),
                        optionC.getText().toString(),
                        optionD.getText().toString()));

                Question question = new Question(questionText.getText().toString(),
                        options, hint.getText().toString(), answer.getSelectedItemPosition(),CREATED_BY);

                databaseReference.push().setValue(question);

                Toast.makeText(AddQuestion.this, "Question added sucessfully!",
                        Toast.LENGTH_SHORT).show();
                clearInputs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clearInputs() {

        questionText.setText("");
        optionA.setText("");
        optionB.setText("");
        optionC.setText("");
        optionD.setText("");
        hint.setText("");
        answer.setSelection(0);
    }
}