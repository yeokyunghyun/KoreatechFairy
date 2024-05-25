package com.example.koreatechfairy4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.example.koreatechfairy4.constants.MajorLink;
import com.example.koreatechfairy4.service.MyService;
import com.example.koreatechfairy4.util.NotificationHelper;
import com.example.koreatechfairy4.util.SharedPreferencesManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText notify_title, notify_message;
    private Button notify_btn, notify_move;
    private NotificationHelper notificationHelper;
    // 알림 기능

    private String userId, userMajor;
    private ConstraintLayout notify_button, schedule_button;
    private Button my_page_button, logout_button, button6, app_info;

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

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("KoreatechFairy4/User/" + userId);
        userRef.child("major").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 데이터가 존재하면, 전공 설정
                    userMajor = dataSnapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });


        // button
        my_page_button = findViewById(R.id.my_page_button);
        logout_button = findViewById(R.id.logout_button);
        notify_button = findViewById(R.id.notify_button);
        schedule_button = findViewById(R.id.schedule_button);
        app_info = findViewById(R.id.app_info);
        button6 = (Button) findViewById(R.id.button6);
        notify_move = findViewById(R.id.notify_move);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = MajorLink.valueOf(userMajor).link();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });

        notify_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://portal.koreatech.ac.kr/ctt/bb/bulletin?b=14"));
                startActivity(browserIntent);
            }
        });
        userId = getIntent().getStringExtra("userId");

        notify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotifyActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        app_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AppInfoActivity.class);
                startActivity(intent);
            }
        });
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


        switchActivityWithUserId(schedule_button, ScheduleActivity.class, userId);
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