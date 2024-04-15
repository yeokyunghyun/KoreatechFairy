package com.example.koreatechfairy4;

import android.os.Bundle;
import android.util.Log;

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

    private RecyclerView recyclerViewCommon, recyclerViewUniver, recyclerViewTrain;
    private RecyclerView.Adapter commonAdapter, univerAdapter, trainAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<NotifyDto> commonNotifyList, univerNotifyList, trainNotifyList;
    private Map<Integer, ArrayList<NotifyDto>> notifyMap;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

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

        recyclerViewCommon = findViewById(R.id.notify_common);
        recyclerViewUniver = findViewById(R.id.notify_univer);
        recyclerViewTrain = findViewById(R.id.notify_train);

        recyclerViewCommon.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화
        recyclerViewUniver.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화
        recyclerViewTrain.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화

        notifyMap = new HashMap<>();
        notifyMap.put(0, new ArrayList<>());
        notifyMap.put(1, new ArrayList<>());
        notifyMap.put(2, new ArrayList<>());

        recyclerViewCommon.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUniver.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrain.setLayoutManager(new LinearLayoutManager(this));

        commonNotifyList = new ArrayList<>(); // 공지사항 객체를 담을 어레이 리스트 (어댑터 쪽으로 날림)

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
                univerAdapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                trainAdapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // DB를 가져오던 중 에러 발생 시
                Log.e("NotifyActivity", String.valueOf(error.toException()));
            }
        });

        commonAdapter = new NotifyAdapter(notifyMap.get(0), this);
        univerAdapter = new NotifyAdapter(notifyMap.get(1), this);
        trainAdapter = new NotifyAdapter(notifyMap.get(2), this);
        recyclerViewCommon.setAdapter(commonAdapter);
        recyclerViewUniver.setAdapter(univerAdapter);
        recyclerViewTrain.setAdapter(trainAdapter);

    }
}