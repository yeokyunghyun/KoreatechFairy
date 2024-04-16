package com.example.koreatechfairy4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VolunNotifyActivity extends AppCompatActivity {

    private Button btn_main, btn_common, btn_benefit, btn_job, btn_academic, btn_employ, btn_train, btn_dormi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_volun_notify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.volunNotifyPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /*버튼 관련 부분*/
        btn_main = findViewById(R.id.btn_main);
        btn_common = findViewById(R.id.btn_common);
        btn_benefit = findViewById(R.id.btn_benefit);
        btn_job = findViewById(R.id.btn_job);
        btn_academic = findViewById(R.id.btn_academic);
        btn_employ = findViewById(R.id.btn_employ);
        btn_train = findViewById(R.id.btn_train);
        btn_dormi = findViewById(R.id.btn_dormi);

        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VolunNotifyActivity.this, NotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_common.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VolunNotifyActivity.this, CommonNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_benefit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VolunNotifyActivity.this, BenefitNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VolunNotifyActivity.this, JobNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_academic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VolunNotifyActivity.this, AcademicNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_employ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VolunNotifyActivity.this, EmployNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VolunNotifyActivity.this, TrainNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_dormi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VolunNotifyActivity.this, DormiNotifyActivity.class);
                startActivity(intent);
            }
        });
    }
}