package com.example.koreatechfairy4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koreatechfairy4.adapter.NotifyAdapter;
import com.example.koreatechfairy4.dto.NotifyDto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotifyActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCommon, recyclerViewAcademic, recyclerViewTrain;
    private RecyclerView.Adapter commonAdapter, academicAdapter, trainAdapter;
    private Map<Integer, ArrayList<NotifyDto>> notifyMap;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    
    private Button btn_common, btn_benefit, btn_job, btn_academic, btn_employ, btn_train, btn_volun, btn_dormi;


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
        
        /*버튼 관련 부분*/
        btn_common = findViewById(R.id.btn_common);
        btn_benefit = findViewById(R.id.btn_benefit);
        btn_job = findViewById(R.id.btn_job);
        btn_academic = findViewById(R.id.btn_academic);
        btn_employ = findViewById(R.id.btn_employ);
        btn_train = findViewById(R.id.btn_train);
        btn_volun = findViewById(R.id.btn_volun);
        btn_dormi = findViewById(R.id.btn_dormi);

        btn_common.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotifyActivity.this, CommonNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_benefit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotifyActivity.this, BenefitNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotifyActivity.this, JobNotifyActivity.class);
                startActivity(intent); // 인텐트를 사용하여 활동을 시작합니다.
            }
        });
        btn_academic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotifyActivity.this, AcademicNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_employ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotifyActivity.this, EmployNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotifyActivity.this, TrainNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_volun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotifyActivity.this, VolunNotifyActivity.class);
                startActivity(intent);
            }
        });
        btn_dormi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotifyActivity.this, DormiNotifyActivity.class);
                startActivity(intent);
            }
        });
        
        
        
        
        
        /*데이터 가져오는 부분*/

        recyclerViewCommon = findViewById(R.id.notify_keyword);    //얘는 키워드 목록으로
        //recyclerViewAcademic = findViewById(R.id.notify_academic);    //나중에 키워드 관련 공지로 바꿔야 함
        //recyclerViewTrain = findViewById(R.id.notify_train);

        //recyclerViewCommon.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화
        //recyclerViewAcademic.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화
        //recyclerViewTrain.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화

        notifyMap = new HashMap<>();
        notifyMap.put(0, new ArrayList<>());
        notifyMap.put(1, new ArrayList<>());
        notifyMap.put(2, new ArrayList<>());

        recyclerViewCommon.setLayoutManager(new LinearLayoutManager(this));
        //recyclerViewAcademic.setLayoutManager(new LinearLayoutManager(this));
        //recyclerViewTrain.setLayoutManager(new LinearLayoutManager(this));

        database = FirebaseDatabase.getInstance(); // 파이어베이스 DB 연동

        databaseReference = database.getReference("KoreatechFairy4/NotifyDto"); //DB 테이블 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 파이어베이스 DB의 데이터를 받아오는 곳
                for (int key : notifyMap.keySet()) {
                    notifyMap.get(key).clear();
                }

                //notifyMap.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) { // 반복문으로 데이터 List 추출
                    NotifyDto notify = dataSnapshot.getValue(NotifyDto.class); // 만들어뒀던 NotifyDto 객체에 데이터를 담는다
                    if (notify != null && notifyMap.containsKey(notify.getDomain())) {
                        notifyMap.get(notify.getDomain()).add(notify);
                    }
                }
                commonAdapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                //academicAdapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                //trainAdapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // DB를 가져오던 중 에러 발생 시
                Log.e("NotifyActivity", String.valueOf(error.toException()));
            }
        });

        commonAdapter = new NotifyAdapter(notifyMap.get(0), this);
        //academicAdapter = new NotifyAdapter(notifyMap.get(1), this);
        //trainAdapter = new NotifyAdapter(notifyMap.get(2), this);
        recyclerViewCommon.setAdapter(commonAdapter);
        //recyclerViewAcademic.setAdapter(academicAdapter);
       // recyclerViewTrain.setAdapter(trainAdapter);

    }
}