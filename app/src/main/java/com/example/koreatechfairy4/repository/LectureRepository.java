package com.example.koreatechfairy4.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.koreatechfairy4.dto.LectureDto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LectureRepository {
    private FirebaseDatabase database;
    DatabaseReference userRef;
    private String reference;

    public LectureRepository(String reference) {
        this.reference = reference;
        this.database = FirebaseDatabase.getInstance();
        userRef = database.getReference(reference);
    }

    public void remove() {
        userRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ScheduleActivity", "New data has been added successfully.");
            } else {
                Log.e("ScheduleActivity", "Failed to delete existing data: " + task.getException());
            }
        });
    }

    public void save(List<LectureDto> lectures) {
        for(LectureDto lecture : lectures) {
            //lecture 1개씩 데이터 베이스에 저장
            String key = userRef.child("lectureList").push().getKey();
            if (key != null) {
                userRef.child("lectureList").child(key).setValue(lecture).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("LectureRepository", "Lecture added successfully: ");
                    } else {
                        Log.e("LectureRepository", "Failed to add lecture: " + task.getException());
                    }
                });
            }
            //파이어베이스에 구분대로 나누기
            classifyLecture(lecture);
        }
    }


    private void classifyLecture(LectureDto lecture){
        //userRef에는 2024/1까지 저장이 되어있고 여기다가 HRD/ 교양/ 전공으로 나눠서 분류하면 됨
    }

    public void getLectureDtoList(final DataCallback callback) {
        userRef.child("lectureList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LectureDto> lectures = new ArrayList<>();
                for (DataSnapshot lectureSnapshot : dataSnapshot.getChildren()) {
                    LectureDto lecture = lectureSnapshot.getValue(LectureDto.class);
                    if (lecture != null) {
                        lectures.add(lecture);
                    }
                }
                callback.onCallback(lectures);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("LectureRepository", "Failed to read data: " + databaseError.toException());
            }
        });
    }

    public interface DataCallback {
        void onCallback(List<LectureDto> lectureList);
    }

}
