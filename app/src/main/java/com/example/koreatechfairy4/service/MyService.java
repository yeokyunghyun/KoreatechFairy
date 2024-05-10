package com.example.koreatechfairy4.service;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.koreatechfairy4.DetailNotifyActivity;
import com.example.koreatechfairy4.LoginActivity;
import com.example.koreatechfairy4.MainActivity;
import com.example.koreatechfairy4.NotifyActivity;
import com.example.koreatechfairy4.R;
import com.example.koreatechfairy4.constants.NotifyDomain;
import com.example.koreatechfairy4.dto.NotifyDto;
import com.example.koreatechfairy4.util.NotificationHelper;
import com.example.koreatechfairy4.util.NotifyCrawler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyService extends Service {

    private DatabaseReference databaseReference;
    private List<NotifyDto> notifies;
    private String jobLink = "https://job.koreatech.ac.kr/jobs/notice/jobNoticeList.aspx?page=";
    private NotificationHelper notificationHelper;
    private ArrayList<String> keywords;
    private String userId;
    private static int notifyNum = 1;
    private static final int NOTIFICATION_ID = 5;
    private static final String CHANNEL_ID = "MyServiceChannel";

    private static final int INTERVAL = 60 * 1000; //1분
    private final Handler handler = new Handler();
    private Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            updateNotifyDb();
            handler.postDelayed(this, INTERVAL);
        }
    };

    private String groupKey1 = "groupKey1";
    private Intent notificationIntent;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopSelf();
            return START_STICKY;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            notificationIntent = new Intent(this, MainActivity.class);
        }
        else {
            notificationIntent = new Intent(this, LoginActivity.class);
        }

        createNotificationChannel();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("KoreatechFairy")
                .setContentText("어플리케이션이 실행중입니다.")
                .setSmallIcon(R.drawable.smallfairy)
                .setContentIntent(pendingIntent)
                .setGroup(groupKey1)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        userId = intent.getStringExtra("userId");
        updateNotifyDb();

        handler.postDelayed(updateTask, INTERVAL);
        // do heavy work on a background thread
        // stopSelf();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(updateTask);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Service Channel";
            String description = "This is My Service Channel";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void updateNotifyDb() {
        databaseReference = FirebaseDatabase.getInstance().getReference("KoreatechFairy4/NotifyDto");
        notificationHelper = new NotificationHelper(this);

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
                            });

                            for (NotifyDto crawledNotify : notifies) {
                                if (!firebaseNotifies.contains(crawledNotify)) {
                                    loadKeywords(crawledNotify);
                                }
                                domainRef.child("Notify_" + formatCount(count++)).setValue(crawledNotify);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("MyService", "Unexpected error in updateNotifyDb thread", e);
            }
        }).start();


        new Thread(() -> {
            try {
                DatabaseReference jobRef = databaseReference.child("JOB");
                notifies = NotifyCrawler.getJobNotice(jobLink);

                jobRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = 1;
                        List<NotifyDto> firebaseNotifies = new ArrayList<>();
                        task.getResult().getChildren().forEach(snapshot -> {
                            NotifyDto notify = snapshot.getValue(NotifyDto.class);
                            firebaseNotifies.add(notify);
                        });

                        for (NotifyDto crawledNotify : notifies) {
                            if (!firebaseNotifies.contains(crawledNotify)) {
                                loadKeywords(crawledNotify);
                            }
                            jobRef.child("Notify_" + formatCount(count++)).setValue(crawledNotify);
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("MyService", "Unexpected error in updateNotifyDb thread", e);
            }
        }).start();
    }

    private synchronized void sendOnChannel(NotifyDto notify) {
        String title = "새로운 공지사항이 등록되었습니다.";
        String msg = notify.getTitle();

        Intent intent = new Intent(this, DetailNotifyActivity.class);
        intent.putExtra("title", notify.getTitle());
        intent.putExtra("date", notify.getDate());
        intent.putExtra("html", notify.getHtml());
        intent.putExtra("imgUrls", notify.getImgUrls());
        intent.putExtra("baseUrl", notify.getBaseUrl());
        intent.putExtra("author", notify.getAuthor());
        PendingIntent pdIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String groupKey = "groupKey_" + System.currentTimeMillis();

        NotificationCompat.Builder nb = notificationHelper.getChannel1Notification(title, msg);
        nb.setContentIntent(pdIntent)
                .setGroup(groupKey);
        notificationHelper.getManager().notify(notifyNum++, nb.build());
    }

    private boolean compareKeyword(NotifyDto notify) {
        for (String keyword : keywords) {
            if (notify != null && (notify.getTitle().contains(keyword) || notify.getText().contains(keyword))) {
                return true;
            }
        }
        return false;
    }

    private void loadKeywords(NotifyDto notify) {
        DatabaseReference keywordReference = FirebaseDatabase.getInstance().getReference("KoreatechFairy4/User/" + userId + "/keyword");
        keywords = new ArrayList<>();
        keywordReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keywords.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String keyword = snapshot.getValue(String.class);
                    keywords.add(keyword);
                }
                notifyKeyword(notify);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("KeywordFragment", "Failed to read keywords", databaseError.toException());
            }
        });
    }

    private void notifyKeyword(NotifyDto notify) {
        if (compareKeyword(notify)) {
            sendOnChannel(notify);
            showNotification(notificationIntent);
        }
    }

    private String formatCount(int count) {
        return String.format("%02d", count);
    }

    @SuppressLint("ForegroundServiceType")
    private void showNotification(Intent notificationIntent) {
        createNotificationChannel();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("KoreatechFairy")
                .setContentText("어플리케이션이 실행중입니다.")
                .setSmallIcon(R.drawable.smallfairy)
                .setContentIntent(pendingIntent)
                .setGroup(groupKey1)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }
}