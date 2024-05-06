package com.example.koreatechfairy4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.koreatechfairy4.domain.Lecture;
import com.example.koreatechfairy4.dto.GradeDto;
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
    private TextView tv1, tv2, tv3;

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
                        GradeDto userGrade = LectureCrawler.crawlLecture(getApplicationContext(), uri);
                        setUserGrade(userRef, userGrade); // User의 전공학점, 전체학점, 이수학점을 집어넣음

                        userRef.child("majorGrade").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // 데이터가 존재하면, 그 값을 가져와서 TextView에 설정
                                    Double data = dataSnapshot.getValue(Double.class);
                                    if (data == 0) {
                                        tv1.setText("데이터 없음");
                                    } else {
                                        tv1.setText(String.valueOf(data));
                                    }
                                } else {
                                    tv1.setText("데이터 x");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                                Log.w("TAG", "Failed to read value.", databaseError.toException());
                                tv1.setText("에러 발생");
                            }
                        });

                        userRef.child("avgGrade").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // 데이터가 존재하면, 그 값을 가져와서 TextView에 설정
                                    Double data = dataSnapshot.getValue(Double.class);
                                    if (data == 0) {
                                        tv2.setText("데이터 없음");
                                    } else {
                                        tv2.setText(String.valueOf(data));
                                    }
                                } else {
                                    tv2.setText("데이터 x");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                                Log.w("TAG", "Failed to read value.", databaseError.toException());
                                tv2.setText("에러 발생");
                            }
                        });

                        userRef.child("totalCredit").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // 데이터가 존재하면, 그 값을 가져와서 TextView에 설정
                                    int data = dataSnapshot.getValue(Integer.class);
                                    if (data == 0) {
                                        tv3.setText("데이터 없음");
                                    } else {
                                        tv3.setText(String.valueOf(data));
                                    }
                                } else {
                                    tv3.setText("데이터 x");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                                Log.w("TAG", "Failed to read value.", databaseError.toException());
                                tv3.setText("에러 발생");
                            }
                        });
                    }
                });

        schedule_register = findViewById(R.id.schedule_register);
        schedule_register.setOnClickListener(v -> openDocument());

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);

        userRef.child("majorGrade").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 데이터가 존재하면, 그 값을 가져와서 TextView에 설정
                    Double data = dataSnapshot.getValue(Double.class);
                    if (data == 0) {
                        tv1.setText("데이터 없음");
                    } else {
                        tv1.setText(String.valueOf(data));
                    }
                } else {
                    tv1.setText("데이터 x");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                Log.w("TAG", "Failed to read value.", databaseError.toException());
                tv1.setText("에러 발생");
            }
        });

        userRef.child("avgGrade").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 데이터가 존재하면, 그 값을 가져와서 TextView에 설정
                    Double data = dataSnapshot.getValue(Double.class);
                    if (data == 0) {
                        tv2.setText("데이터 없음");
                    } else {
                        tv2.setText(String.valueOf(data));
                    }
                } else {
                    tv2.setText("데이터 x");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                Log.w("TAG", "Failed to read value.", databaseError.toException());
                tv2.setText("에러 발생");
            }
        });

        userRef.child("totalCredit").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 데이터가 존재하면, 그 값을 가져와서 TextView에 설정
                    int data = dataSnapshot.getValue(Integer.class);
                    if (data == 0) {
                        tv3.setText("데이터 없음");
                    } else {
                        tv3.setText(String.valueOf(data));
                    }
                } else {
                    tv3.setText("데이터 x");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                Log.w("TAG", "Failed to read value.", databaseError.toException());
                tv3.setText("에러 발생");
            }
        });
    }


    private void openDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // XLSX 파일 타입
        getContentLauncher.launch(intent);
    }


    private void setUserGrade(DatabaseReference userRef, GradeDto userGrade) {
        userRef.child("majorGrade").setValue(userGrade.getMajorGrade());
        userRef.child("avgGrade").setValue(userGrade.getAvgGrade());
        userRef.child("totalCredit").setValue(userGrade.getTotalGrade());
    }

}

