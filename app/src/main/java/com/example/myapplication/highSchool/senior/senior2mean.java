package com.example.myapplication.highSchool.senior;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.media.MediaPlayer;
import android.widget.ProgressBar;

import com.example.myapplication.R;
import com.example.myapplication.ResultActivity;
import com.example.myapplication.SettingsManger;
import com.example.myapplication.Word;

public class senior2mean extends AppCompatActivity {
    private TextView txtQuestion;
    private Button btnOption1, btnOption2, btnOption3, btnOption4, back;
    private ProgressBar progressBar;
    private CountDownTimer countDownTimer;
    private MediaPlayer correctSound, wrongSound;
    private List<Word> quizWords;
    private int currentIndex = 0;
    private int score = 0;
    private int quizTime; // 제한 시간 (밀리초 단위)
    private boolean isSoundEnabled; // 소리 설정
    private boolean exitFlag = false; // 🔹 back 버튼 클릭 여부를 확인하는 플래그
    private SettingsManger settingsManager; // 설정 관리자
    private Word currentWord; // 현재 단어 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.senior2mean);

        settingsManager = new SettingsManger(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loadSettings();  // 🔹 설정 값을 먼저 불러옴
        initializeViews();  // 🔹 UI 요소 초기화 (설정 값 적용 후)
        initializeMedia();  // 🔹 소리 관련 요소 초기화

        // 🔹 퀴즈 단어 리스트 불러오기
        quizWords = senior2wordlist.getShuffledWords();
        if (quizWords == null || quizWords.isEmpty()) {
            Toast.makeText(this, "퀴즈 문제가 없습니다!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadNextQuestion();  // 🔹 첫 번째 문제 로드

        // 🔹 뒤로가기 버튼 클릭 시 종료 (결과 화면 이동 방지)
        back.setOnClickListener(view -> {
            exitFlag = true; // 🔹 back 버튼이 눌렸음을 표시
            Toast.makeText(senior2mean.this, "퀴즈를 종료합니다.", Toast.LENGTH_SHORT).show();
            finish(); // 현재 액티비티 종료
        });
    }

    // 🔹 설정 값 불러오기 (제한 시간 및 소리 설정)
    private void loadSettings() {
        quizTime = settingsManager.getQuizTime(); // 설정된 제한 시간 가져오기
        isSoundEnabled = settingsManager.isSoundEnabled(); // 소리 설정 적용
    }

    // 🔹 UI 요소 초기화
    private void initializeViews() {
        progressBar = findViewById(R.id.progressBar1);
        progressBar.setMax(quizTime); // 🔹 설정한 제한 시간에 맞게 progressBar 최대값 설정

        txtQuestion = findViewById(R.id.txtQ);
        btnOption1 = findViewById(R.id.btnO5);
        btnOption2 = findViewById(R.id.btnO6);
        btnOption3 = findViewById(R.id.btnO7);
        btnOption4 = findViewById(R.id.btnO8);
        back = findViewById(R.id.back4);
    }

    // 🔹 오디오 파일 초기화
    private void initializeMedia() {
        correctSound = MediaPlayer.create(this, R.raw.right);
        wrongSound = MediaPlayer.create(this, R.raw.error);
    }

    // 🔹 새로운 문제를 불러오는 메서드
    private void loadNextQuestion() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // 🔹 문제 리스트가 비어 있는 경우 오류 방지
        if (quizWords == null || quizWords.isEmpty()) {
            Toast.makeText(this, "퀴즈 문제가 없습니다!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 🔹 currentIndex가 범위를 초과하지 않도록 체크
        if (currentIndex >= quizWords.size()) {
            if (!exitFlag) { // 🔹 back 버튼이 눌리지 않았을 때만 결과 화면 이동
                moveToResultScreen();
            }
            return;
        }

        // 🔹 현재 문제 설정
        currentWord = quizWords.get(currentIndex);
        if (currentWord == null) {
            Toast.makeText(this, "문제 데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            moveToResultScreen();
            return;
        }

        txtQuestion.setText("단어: " + currentWord.getWord());

        List<String> options = new ArrayList<>();
        options.add(currentWord.getMeaning());

        while (options.size() < 4) {
            String randomMeaning = quizWords.get((int) (Math.random() * quizWords.size())).getMeaning();
            if (!options.contains(randomMeaning)) {
                options.add(randomMeaning);
            }
        }

        Collections.shuffle(options);

        Button[] buttons = {btnOption1, btnOption2, btnOption3, btnOption4};
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] != null) {
                buttons[i].setText(options.get(i));
                setButtonClickListener(buttons[i], options.get(i));
            }
        }

        startTimer();
    }

    // 🔹 버튼 클릭 시 정답 여부 확인
    private void setButtonClickListener(Button button, String answer) {
        button.setOnClickListener(v -> {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            if (answer.equals(currentWord.getMeaning())) {
                score++;
                Toast.makeText(senior2mean.this, "정답!", Toast.LENGTH_SHORT).show();
                if (isSoundEnabled && correctSound != null) correctSound.start();
            } else {
                Toast.makeText(senior2mean.this, "오답!", Toast.LENGTH_SHORT).show();
                if (isSoundEnabled && wrongSound != null) wrongSound.start();
            }

            currentIndex++;
            loadNextQuestion();
        });
    }

    // 🔹 제한 시간 타이머 실행
    private void startTimer() {
        progressBar.setProgress(quizTime); // 🔹 제한 시간 설정

        countDownTimer = new CountDownTimer(quizTime, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress((int) millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (!exitFlag) { // 🔹 back 버튼이 눌리지 않았을 때만 결과 화면 이동
                    moveToResultScreen();
                }
            }
        };
        countDownTimer.start();
    }

    // 🔹 결과 화면 이동 (back 버튼이 눌리지 않은 경우만)
    private void moveToResultScreen() {
        if (!exitFlag) { // 🔹 back 버튼이 눌리지 않았을 때만 실행
            Intent intent = new Intent(senior2mean.this, ResultActivity.class);
            intent.putExtra("SCORE", score);
            intent.putExtra("TOTAL", quizWords.size());
            startActivity(intent);
            finish();
        }
    }
}
