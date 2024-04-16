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

public class BenefitNotifyActivity extends AppCompatActivity {

    private Button btn_main, btn_common, btn_job, btn_academic, btn_employ, btn_train, btn_volun, btn_dormi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_benefit_notify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.benefitNotifyPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /*버튼 관련 부분*/
        btn_main = findViewById(R.id.btn_main);
        btn_common = findViewById(R.id.btn_common);
        btn_job = findViewById(R.id.btn_job);
        btn_academic = findViewById(R.id.btn_academic);
        btn_employ = findViewById(R.id.btn_employ);
        btn_train = findViewById(R.id.btn_train);
        btn_volun = findViewById(R.id.btn_volun);
        btn_dormi = findViewById(R.id.btn_dormi);

        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BenefitNotifyActivity.this, NotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_common.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BenefitNotifyActivity.this, CommonNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BenefitNotifyActivity.this, JobNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_academic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BenefitNotifyActivity.this, AcademicNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_employ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BenefitNotifyActivity.this, EmployNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BenefitNotifyActivity.this, TrainNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_volun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BenefitNotifyActivity.this, VolunNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_dormi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BenefitNotifyActivity.this, DormiNotifyActivity.class);
                startActivity(intent);
            }
        });
    }
}