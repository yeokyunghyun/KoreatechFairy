package com.example.koreatechfairy4;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.koreatechfairy4.domain.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MyPageActivity extends AppCompatActivity {

    private TextView profile_name, profile_st_id, profile_major;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        // UI 컴포넌트 초기화
        profile_name = findViewById(R.id.profile_name);
        profile_st_id = findViewById(R.id.profile_st_id);
        profile_major = findViewById(R.id.profile_major);

        // Firebase 인스턴스 가져오기
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("KoreatechFairy4/User");

        // 현재 로그인된 사용자의 ID 가저오기
        String currentUserId = getIntent().getStringExtra("userId");

        // 데이터 읽기
        mDatabaseRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user != null) {
                    profile_name.setText(user.getName());
                    profile_st_id.setText(user.getStudentId());
                    profile_major.setText(user.getMajor());
                }
                else {
                    Toast.makeText(MyPageActivity.this, "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyPageActivity.this, "데이터 로드 실패: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}