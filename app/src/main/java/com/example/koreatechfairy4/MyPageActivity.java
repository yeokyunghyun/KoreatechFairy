package com.example.koreatechfairy4;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.koreatechfairy4.dto.GradeDto;
import com.example.koreatechfairy4.util.LectureCrawler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyPageActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> getContentLauncher;
    private Button schedule_register, helpButton;
    private TextView name, studentId, major;
    private TextView totalCredit, totalGrade, majorGrade;
    private TextView textViewHelpTrigger;
    private TextView textViewHelpContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

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
                                        totalCredit.setText("데이터 없음");
                                    } else {
                                        totalCredit.setText(String.valueOf(data));
                                    }
                                } else {
                                    totalCredit.setText("데이터 x");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                                Log.w("TAG", "Failed to read value.", databaseError.toException());
                                totalCredit.setText("에러 발생");
                            }
                        });

                        userRef.child("avgGrade").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // 데이터가 존재하면, 그 값을 가져와서 TextView에 설정
                                    Double data = dataSnapshot.getValue(Double.class);
                                    if (data == 0) {
                                        totalGrade.setText("데이터 없음");
                                    } else {
                                        totalGrade.setText(String.valueOf(data));
                                    }
                                } else {
                                    totalGrade.setText("데이터 x");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                                Log.w("TAG", "Failed to read value.", databaseError.toException());
                                totalGrade.setText("에러 발생");
                            }
                        });

                        userRef.child("totalCredit").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // 데이터가 존재하면, 그 값을 가져와서 TextView에 설정
                                    int data = dataSnapshot.getValue(Integer.class);
                                    if (data == 0) {
                                        majorGrade.setText("데이터 없음");
                                    } else {
                                        majorGrade.setText(String.valueOf(data));
                                    }
                                } else {
                                    majorGrade.setText("데이터 x");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                                Log.w("TAG", "Failed to read value.", databaseError.toException());
                                majorGrade.setText("에러 발생");
                            }
                        });
                    }
                });

        schedule_register = findViewById(R.id.schedule_register);
        schedule_register.setOnClickListener(v -> openDocument());

        name = findViewById(R.id.name);
        studentId = findViewById(R.id.student_id);
        major = findViewById(R.id.major);

        totalCredit = findViewById(R.id.total_credit);
        totalGrade = findViewById(R.id.total_grade);
        majorGrade = findViewById(R.id.major_grade);

        helpButton = findViewById(R.id.help_button);

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyPageActivity.this);
                builder.setTitle("파일 등록 방법");
                builder.setMessage("아우누리 학사 -> 학적기본사항 -> 성적이력에 있는 xls(액셀)파일을 첨부해주세요");

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 사용자가 '확인' 버튼을 클릭했을 때 실행할 코드
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        userRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 데이터가 존재하면, 그 값을 가져와서 TextView에 설정
                    String data = dataSnapshot.getValue(String.class);
                    if (data == null) {
                        name.setText("데이터 없음");
                    } else {
                        name.setText(data);
                    }
                } else {
                    name.setText("데이터 x");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                Log.w("TAG", "Failed to read value.", databaseError.toException());
                name.setText("에러 발생");
            }
        });

        userRef.child("studentId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 데이터가 존재하면, 그 값을 가져와서 TextView에 설정
                    String data = dataSnapshot.getValue(String.class);
                    if (data == null) {
                        studentId.setText("데이터 없음");
                    } else {
                        studentId.setText(data);
                    }
                } else {
                    studentId.setText("데이터 x");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                Log.w("TAG", "Failed to read value.", databaseError.toException());
                studentId.setText("에러 발생");
            }
        });

        userRef.child("major").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 데이터가 존재하면, 그 값을 가져와서 TextView에 설정
                    String data = dataSnapshot.getValue(String.class);
                    if (data == null) {
                        major.setText("데이터 없음");
                    } else {
                        major.setText(data);
                    }
                } else {
                    major.setText("데이터 x");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                Log.w("TAG", "Failed to read value.", databaseError.toException());
                major.setText("에러 발생");
            }
        });
        userRef.child("avgGrade").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 데이터가 존재하면, 그 값을 가져와서 TextView에 설정
                    Double data = dataSnapshot.getValue(Double.class);
                    Double roundData = Math.round(data * 100.0) / 100.0;

                    if (data == 0) {
                        totalGrade.setText("데이터 없음");
                    } else {
                        totalGrade.setText(String.valueOf(roundData));
                    }
                } else {
                    totalGrade.setText("데이터 x");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                Log.w("TAG", "Failed to read value.", databaseError.toException());
                totalGrade.setText("에러 발생");
            }
        });

        userRef.child("majorGrade").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 데이터가 존재하면, 그 값을 가져와서 TextView에 설정
                    Double data = dataSnapshot.getValue(Double.class);
                    Double roundData = Math.round(data * 100.0) / 100.0;

                    if (data == 0) {
                        majorGrade.setText("데이터 없음");
                    } else {
                        majorGrade.setText(String.valueOf(roundData));
                    }
                } else {
                    majorGrade.setText("데이터 x");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                Log.w("TAG", "Failed to read value.", databaseError.toException());
                majorGrade.setText("에러 발생");
            }
        });

        userRef.child("totalCredit").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 데이터가 존재하면, 그 값을 가져와서 TextView에 설정
                    int data = dataSnapshot.getValue(Integer.class);
                    if (data == 0) {
                        totalCredit.setText("데이터 없음");
                    } else {
                        totalCredit.setText(String.valueOf(data));
                    }
                } else {
                    totalCredit.setText("데이터 x");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                Log.w("TAG", "Failed to read value.", databaseError.toException());
                totalCredit.setText("에러 발생");
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

