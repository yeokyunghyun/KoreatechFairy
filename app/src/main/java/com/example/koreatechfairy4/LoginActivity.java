//로그인 화면
package com.example.koreatechfairy4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; 
    private DatabaseReference mDatabaseRef;
    private EditText login_user_id, login_pw;
    private Button login_login, login_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.my_page_back), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("KoreatechFairy4");

        login_user_id = findViewById(R.id.login_email_id);
        login_pw = findViewById(R.id.login_pw);

        login_login = findViewById(R.id.login_login);
        login_register = findViewById(R.id.login_register);

        login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginId = login_user_id.getText().toString();
                String loginPw = login_pw.getText().toString();
                String loginEmail = loginId + "@koreatech.com";
                //Firebase Auth진행
                mFirebaseAuth.signInWithEmailAndPassword(loginEmail, loginPw)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    //로그인 성공
                                    FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
                                    Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                                    intent.putExtra("userId", currentUser.getUid());
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    if (task.getException() != null) {
                                        Log.e("LoginActivity", "Login failed", task.getException());
                                        Toast.makeText(LoginActivity.this, "로그인에 실패하셨습니다: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }                                }
                            }
                        });
            }
        });

        login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}