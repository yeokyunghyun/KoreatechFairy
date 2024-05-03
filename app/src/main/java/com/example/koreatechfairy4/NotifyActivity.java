package com.example.koreatechfairy4;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.koreatechfairy4.constants.NotifyDomain;
import com.example.koreatechfairy4.dto.NotifyDto;
import com.example.koreatechfairy4.fragment.AcademicNotifyFragment;
import com.example.koreatechfairy4.fragment.BenefitNotifyFragment;
import com.example.koreatechfairy4.fragment.CommonNotifyFragment;
import com.example.koreatechfairy4.fragment.EmployNotifyFragment;
import com.example.koreatechfairy4.fragment.JobNotifyFragment;
import com.example.koreatechfairy4.fragment.KeywordNotifyFragment;
import com.example.koreatechfairy4.util.NotifyCrawler;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotifyActivity extends AppCompatActivity {

    private ImageButton notify_back;
    private Button my_page_button, keywordButton,academicButton, benefitButton, commonButton, employButton, jobButton;
    private DatabaseReference databaseReference;
    private List<NotifyDto> notifies;
    private String jobLink = "https://job.koreatech.ac.kr/jobs/notice/jobNoticeList.aspx?page=";

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

        createNotificationChannel();

        keywordButton = (Button) findViewById(R.id.keyword_button);
        academicButton = (Button) findViewById(R.id.academic_button);
        benefitButton = (Button) findViewById(R.id.benefit_button);
        commonButton = (Button) findViewById(R.id.common_button);
        employButton = (Button) findViewById(R.id.employ_button);
        jobButton = (Button) findViewById(R.id.job_button);

        setClickListenerWithArgs(keywordButton, new KeywordNotifyFragment());
        setClickListener(academicButton, new AcademicNotifyFragment());
        setClickListener(benefitButton, new BenefitNotifyFragment());
        setClickListener(commonButton, new CommonNotifyFragment());
        setClickListener(employButton, new EmployNotifyFragment());
        setClickListener(jobButton, new JobNotifyFragment());

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
                    DatabaseReference domainRef = databaseReference.child(String.valueOf(domain));
                    notifies = NotifyCrawler.getNotice(domain);

                    domainRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            int count = 1;
                            List<NotifyDto> firebaseNotifies = new ArrayList<>();
                            task.getResult().getChildren().forEach(snapshot -> {
                                NotifyDto notify = snapshot.getValue(NotifyDto.class);
                                firebaseNotifies.add(notify);
                                System.out.println("기존 공지: " + notify.getTitle());
                            });

                            for (NotifyDto crawledNotify : notifies) {
                                if (!firebaseNotifies.contains(crawledNotify)) {
                                    sendNotification(crawledNotify);
                                    //domainRef.child("Notify_" + formatCount(count++)).setValue(crawledNotify);
                                    System.out.println("새로운 공지: " + crawledNotify.getTitle());
                                }
                            }
                        }
                    });




                    //insertNotifyData(databaseReference, notifies, domain);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
                notifies = NotifyCrawler.getJobNotice(jobLink);
                int count = 1;
                for (NotifyDto notify : notifies) {
                    databaseReference.child("JOB").child("Notify_" + formatCount(count++)).setValue(notify);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();



        //공지사항 비교
        /*-----------------------------------------------------------------*/


    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notify_001", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(NotifyDto notify) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notify_001")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("New Notification")
                .setContentText(notify.getTitle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
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
            databaseReference.child(String.valueOf(domain)).child("Notify_" + formatCount(count++)).setValue(notify);
        }
    }

    private String formatCount(int count) {
        return String.format("%02d", count);
    }
}