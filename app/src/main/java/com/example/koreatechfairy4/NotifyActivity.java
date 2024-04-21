package com.example.koreatechfairy4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koreatechfairy4.constants.NotifyDomain;
import com.example.koreatechfairy4.dto.NotifyDto;
import com.example.koreatechfairy4.fragment.AcademicNotifyFragment;
import com.example.koreatechfairy4.fragment.BenefitNotifyFragment;
import com.example.koreatechfairy4.fragment.CommonNotifyFragment;
import com.example.koreatechfairy4.fragment.DormiNotifyFragment;
import com.example.koreatechfairy4.fragment.EmployNotifyFragment;
import com.example.koreatechfairy4.fragment.JobNotifyFragment;
import com.example.koreatechfairy4.fragment.KeywordNotifyFragment;
import com.example.koreatechfairy4.fragment.TrainNotifyFragment;
import com.example.koreatechfairy4.fragment.VolunNotifyFragment;
import com.example.koreatechfairy4.util.NotifyCrawler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlinx.coroutines.Job;

public class NotifyActivity extends AppCompatActivity {

    private ImageButton notify_back;
    private Button my_page_button, keywordButton,academicButton, benefitButton, commonButton, dormiButton, employButton, jobButton, trainButton, volunButton;
    private DatabaseReference databaseReference;
    private List<NotifyDto> notifies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.notify), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        keywordButton = (Button) findViewById(R.id.keyword_button);
        academicButton = (Button) findViewById(R.id.academic_button);
        benefitButton = (Button) findViewById(R.id.benefit_button);
        commonButton = (Button) findViewById(R.id.common_button);
        dormiButton = (Button) findViewById(R.id.dormi_button);
        employButton = (Button) findViewById(R.id.employ_button);
        jobButton = (Button) findViewById(R.id.job_button);
        trainButton = (Button) findViewById(R.id.train_button);
        volunButton = (Button) findViewById(R.id.volun_button);

        setClickListenerWithArgs(keywordButton, new KeywordNotifyFragment());
        setClickListener(academicButton, new AcademicNotifyFragment());
        setClickListener(benefitButton, new BenefitNotifyFragment());
        setClickListener(commonButton, new CommonNotifyFragment());
        setClickListener(dormiButton, new DormiNotifyFragment());
        setClickListener(employButton, new EmployNotifyFragment());
        setClickListener(jobButton, new JobNotifyFragment());
        setClickListener(trainButton, new TrainNotifyFragment());
        setClickListener(volunButton, new VolunNotifyFragment());

        notify_back = findViewById(R.id.imgBtn_notify_back);
        my_page_button = findViewById(R.id.btn_notify_mypage);

        notify_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotifyActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        HashMap<String, String> myPageMap = new HashMap<>();
        myPageMap.put("userId", getIntent().getStringExtra("userId"));

        my_page_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotifyActivity.this, MyPageActivity.class);
                for (String key : myPageMap.keySet()) {
                    intent.putExtra(key, myPageMap.get(key));
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        /*데이터 등록하는 부분*/
        databaseReference = FirebaseDatabase.getInstance().getReference("KoreatechFairy4/NotifyDto");
        new Thread(() -> {
            try {
                for (NotifyDomain domain : NotifyDomain.values()) {
                    notifies = NotifyCrawler.getNotice(domain);
                    insertNotifyData(databaseReference, notifies, domain);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();


        /* 데이터 가져오는 부분 */


    }

    private void setClickListener(Button btn, Fragment fragment) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.notify, fragment);
                transaction.commit();
            }
        });
    }

    private void setClickListenerWithArgs(Button btn, Fragment fragment) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("userId", getIntent().getStringExtra("userId"));
                fragment.setArguments(args);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.notify, fragment);
                transaction.commit();
            }
        });
    }

    private void insertNotifyData(DatabaseReference dbRef, List<NotifyDto> notifies, NotifyDomain domain) {
        int count = 1;
        for (NotifyDto notify : notifies) {
            databaseReference.child(String.valueOf(domain)).child("Notify_" + count).setValue(notify);
            ++count;
        }
    }
}