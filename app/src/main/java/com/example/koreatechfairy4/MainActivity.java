package com.example.koreatechfairy4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private ConstraintLayout notify_button, schedule_button;
    private Button my_page_button, logout_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        HashMap<String, String> myPageMap = new HashMap<>();
        myPageMap.put("userId", getIntent().getStringExtra("userId"));

        switchActivity(notify_button, NotifyActivity.class);
        switchActivity(logout_button, LoginActivity.class);
        switchActivity(schedule_button, ScheduleActivity.class);
        switchActivityWithExtra(my_page_button, MyPageActivity.class, myPageMap);


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
                startActivity(intent);
            }
        });
    }
}