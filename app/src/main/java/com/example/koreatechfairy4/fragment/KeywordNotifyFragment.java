package com.example.koreatechfairy4.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.koreatechfairy4.R;
import com.example.koreatechfairy4.adapter.KeywordAdapter;
import com.example.koreatechfairy4.adapter.NotifyAdapter;
import com.example.koreatechfairy4.dto.NotifyDto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class KeywordNotifyFragment extends Fragment {

    private EditText editTextKeyword;
    private Button addButton;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference keywordReference;
    private ArrayList<String> keywords;
    private ArrayList<NotifyDto> notifyList;
    private NotifyAdapter notifyAdapter;
    private KeywordAdapter keywordAdapter;
    private String userId;

    public KeywordNotifyFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyword_notify, container, false);

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }
        editTextKeyword = view.findViewById(R.id.editText);
        addButton = view.findViewById(R.id.add_button);
        notifyAdapter = new NotifyAdapter(notifyList, getContext());
        keywordAdapter = new KeywordAdapter(keywords, getContext());
        DatabaseReference databaseReference = firebaseDatabase.getReference("KoreatechFairy4/User/" + userId);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = editTextKeyword.getText().toString();
                if (!keyword.isEmpty()) {
                    // 데이터베이스에 키워드 추가
                    databaseReference.child("keyword").child(keyword).setValue(keyword);
                    editTextKeyword.setText("");  // 키워드 입력 필드 초기화
                }
            }
        });

        return view;
    }

    private void loadKeywords() {
        DatabaseReference keywordReference = firebaseDatabase.getReference("KoreayechFairy4/User/" + userId + "/keyword");
        keywordReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keywords.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String keyword = snapshot.getValue(String.class);
                    keywords.add(keyword);
                }
                keywordAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("KeywordFragment", "Failed to read keywords", databaseError.toException());
            }
        });
    }

}
