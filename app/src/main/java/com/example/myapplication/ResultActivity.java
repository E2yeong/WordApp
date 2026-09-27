package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView txtScore = findViewById(R.id.txtScore);
        Button btnRetry = findViewById(R.id.btnRetry);

        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 0); // 퀴즈마다 문제 수가 다르므로 퀴즈 화면에서 전달받음
        txtScore.setText("당신의 점수: " + score + "/" + total);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, SubActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
