package com.example.koreatechfairy4;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.koreatechfairy4.R;
import com.example.koreatechfairy4.util.SharedPreferencesManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent로 넘겨받은 데이터를 MainActivity로 전달
                String userId = getIntent().getStringExtra("userId");
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                mainIntent.putExtra("userId", userId);
                startActivity(mainIntent);
                finish();  // SplashActivity 종료
            }
        }, 1000);  // 1000ms = 1초
    }
}