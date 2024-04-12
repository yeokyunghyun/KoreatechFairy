package com.example.koreatechfairy4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.koreatechfairy4.domain.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증처리
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private EditText register_name, register_user_id, register_pw;
    private Spinner register_major, register_st_id;
    private Button register_register, register_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("KoreatechFairy4");

        register_name = findViewById(R.id.register_name);
        register_user_id = findViewById(R.id.register_user_id);
        register_pw = findViewById(R.id.register_pw);

        register_register = findViewById(R.id.register_register);
        register_cancel = findViewById(R.id.register_cancel);

        register_major = findViewById(R.id.register_major);
        register_st_id = findViewById(R.id.register_st_id);

        register_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String registerName = register_name.getText().toString();
                String registerId = register_user_id.getText().toString();
                String registerPw = register_pw.getText().toString();

                String selectedMajor = register_major.getSelectedItem().toString();
                String selectedStudentId = register_st_id.getSelectedItem().toString();

                String registerEmail = changeToEmailForm(registerId);

                //Firebase Auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(registerEmail, registerPw) //Auth에 아이디(이메일 형식으로 바꾼) + 비밀번호
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                    User user = new User(firebaseUser.getUid(), registerName, registerId, registerPw, selectedMajor, selectedStudentId);

                                    mDatabaseRef.child("User").child(firebaseUser.getUid()).setValue(user);
                                    Toast.makeText(RegisterActivity.this, "회원가입에 성공하였습니다!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(RegisterActivity.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    private String changeToEmailForm(String registerId) {
        return registerId + "@koreatech.com";
    }
}