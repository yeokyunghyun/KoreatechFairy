package com.example.koreatechfairy4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.koreatechfairy4.service.MyService;
import com.example.koreatechfairy4.util.NotificationHelper;
import com.example.koreatechfairy4.util.SharedPreferencesManager;

import java.util.HashMap;

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

        userId = getIntent().getStringExtra("userId");
        Intent it = new Intent(this, MyService.class);
        it.putExtra("userId", userId);
        startService(it);

        // button
        my_page_button = findViewById(R.id.my_page_button);
        logout_button = findViewById(R.id.logout_button);
        notify_button = findViewById(R.id.notify_button);
        schedule_button = findViewById(R.id.schedule_button);

        userId = getIntent().getStringExtra("userId");
        HashMap<String, String> myPageMap = new HashMap<>();
        myPageMap.put("userId", userId);

        //switchActivity(notify_button, NotifyActivity.class);

        notify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotifyActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        //switchActivity(logout_button, LoginActivity.class);

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(it);

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                SharedPreferencesManager.clearPreferences(MainActivity.this);
                startActivity(intent);
                finish();
            }
        });

        schedule_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
        switchActivityWithExtra(my_page_button, MyPageActivity.class, myPageMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ActivityLifecycle", getClass().getSimpleName() + " - onResume");
    }

    private <T extends AppCompatActivity> void switchActivity(View my_page_button, Class<T> activity) {
        my_page_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, activity);

                startActivity(intent);
            }
        });
    }

    private <T extends AppCompatActivity> void switchActivityWithExtra(View my_page_button, Class<T> activity, HashMap<String, String> map) {
        my_page_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, activity);
                for(String key : map.keySet()) {
                    intent.putExtra(key, map.get(key));
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }
}