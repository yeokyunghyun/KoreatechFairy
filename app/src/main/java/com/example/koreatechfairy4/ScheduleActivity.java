package com.example.koreatechfairy4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.koreatechfairy4.domain.Lecture;
import com.example.koreatechfairy4.util.LectureCrawler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> getContentLauncher;
    private Button schedule_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        String userId = getIntent().getStringExtra("userId");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("KoreatechFairy4/User/" + userId);

        getContentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();

                        // 이 URI를 사용하여 파일 내용을 읽습니다.
                        ArrayList<Lecture> lectures = LectureCrawler.crawlLecture(getApplicationContext(), uri);
                        deleteLectureIfExists(userRef);
                        insertLectureData(userRef, lectures);
                    }
                });

        schedule_register = findViewById(R.id.schedule_register);
        schedule_register.setOnClickListener(v -> openDocument());
    }

    private void openDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // XLSX 파일 타입
        getContentLauncher.launch(intent);
    }

    private void deleteLectureIfExists(DatabaseReference userRef) {
        userRef.child("lectures").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // lecture 데이터가 존재하면 데이터 삭제
                if (dataSnapshot.exists()) {
                    dataSnapshot.getRef().removeValue()
                            .addOnSuccessListener(aVoid -> {
                                // 성공적으로 삭제됐을 때
                                System.out.println("Lecture data was deleted successfully.");
                            })
                            .addOnFailureListener(e -> {
                                // 삭제 실패
                                System.err.println("Failed to delete lecture data: " + e.getMessage());
                            });
                } else {
                    // lecture 데이터가 존재하지 않는 경우
                    System.out.println("No lecture data to delete.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 오류 처리
                System.err.println("Database error: " + databaseError.getMessage());
            }
        });
    }

    private void insertLectureData(DatabaseReference userRef, ArrayList<Lecture> lectures) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (Lecture lecture : lectures) {
                    userRef.child("lectures").child(lecture.getLectureName()).setValue(lecture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Database error: " + error.getMessage());
            }

        });
    }
}

