package edu.neu.madcourse.cs5520_finalproject_team26;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard  extends AppCompatActivity implements View.OnClickListener  {

    private ImageView firstPositionAvatar;
    private ImageView secondPositionAvatar;
    private ImageView thirdPositionAvatar;
    private TextView firstPositionText;
    private TextView secondPositionText;
    private TextView thirdPositionText;
    private ListView rankList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_leaderboard);
        firstPositionText = findViewById(R.id.first_position_text);
        secondPositionText = findViewById(R.id.second_position_text);
        thirdPositionText = findViewById(R.id.third_position_text);

        firstPositionAvatar = findViewById(R.id.first_position_avatar);
        secondPositionAvatar = findViewById(R.id.second_position_avatar);
        thirdPositionAvatar = findViewById(R.id.third_position_avatar);

        rankList = findViewById(R.id.rank_list);

        showOtherRanks();
    }

    private void showOtherRanks() {
        List<String> list = new ArrayList();






        ArrayAdapter<String> array;
        array = new ArrayAdapter<String>(
                this,
                R.layout.rank_item,
                list);
        rankList.setAdapter(array);
    }

    @Override
    public void onClick(View v) {

    }
}
