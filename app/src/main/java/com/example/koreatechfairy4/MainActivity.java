package com.example.koreatechfairy4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.koreatechfairy4.util.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    private EditText notify_title, notify_message;
    private Button notify_btn;
    private NotificationHelper notificationHelper;
    // 알림 기능

    private String userId;
    private ConstraintLayout notify_button, schedule_button;
    private Button my_page_button, logout_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ActivityLifecycle", getClass().getSimpleName() + " - onCreate");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // button
        my_page_button = findViewById(R.id.my_page_button);
        logout_button = findViewById(R.id.logout_button);
        notify_button = findViewById(R.id.notify_button);
        schedule_button = findViewById(R.id.schedule_button);

        userId = getIntent().getStringExtra("userId");

        notify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotifyActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        switchActivity(logout_button, LoginActivity.class);
        switchActivity(schedule_button, ScheduleActivity.class);
        switchActivityWithUserId(my_page_button, MyPageActivity.class, userId);

    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ActivityLifecycle", getClass().getSimpleName() + " - onResume");
    }

    private <T extends AppCompatActivity> void switchActivity(View button, Class<T> activity) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, activity);
                startActivity(intent);
            }
        });
    }

    private <T extends AppCompatActivity> void switchActivityWithUserId(View my_page_button, Class<T> activity, String userId) {
        my_page_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, activity);
                intent.putExtra("userId", userId);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }
}