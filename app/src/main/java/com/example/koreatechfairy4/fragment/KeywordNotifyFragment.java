package com.example.koreatechfairy4.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koreatechfairy4.R;
import com.example.koreatechfairy4.adapter.KeywordAdapter;
import com.example.koreatechfairy4.adapter.KeywordNotifyAdapter;
import com.example.koreatechfairy4.adapter.NotifyAdapter;
import com.example.koreatechfairy4.constants.NotifyDomain;
import com.example.koreatechfairy4.dto.NotifyDto;
import com.example.koreatechfairy4.util.NotifyItemDecoration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class KeywordNotifyFragment extends Fragment {

    private RecyclerView keywordRecyclerView, notifyRecyclerView;
    private EditText editTextKeyword;
    private Button addButton;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private ArrayList<String> keywords;
    private ArrayList<NotifyDto> notifyList;
    private KeywordNotifyAdapter keywordNotifyAdapter;
    private KeywordAdapter keywordAdapter;
    private String userId;
    private int count = 1;
    private ImageButton deleteKeyword, deleteNotify;

    public KeywordNotifyFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyword_notify, container, false);

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }


        keywordRecyclerView = view.findViewById(R.id.input_keyword);
        keywordRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notifyRecyclerView = view.findViewById(R.id.notify_keyword);
        notifyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        editTextKeyword = view.findViewById(R.id.editText);
        addButton = view.findViewById(R.id.add_button);

        keywords = new ArrayList<>();
        keywordAdapter = new KeywordAdapter(keywords, getContext(), userId);

        notifyList = new ArrayList<>();
        keywordNotifyAdapter = new KeywordNotifyAdapter(notifyList, getContext(), userId);

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

        int spaceInPixels = 5;
        keywordRecyclerView.addItemDecoration(new NotifyItemDecoration(spaceInPixels));
        notifyRecyclerView.addItemDecoration(new NotifyItemDecoration(spaceInPixels));

        loadKeywords();

        keywordRecyclerView.setAdapter(keywordAdapter);
        notifyRecyclerView.setAdapter(keywordNotifyAdapter);

        return view;
    }

    private void loadKeywords() {
        DatabaseReference keywordReference = firebaseDatabase.getReference("KoreatechFairy4/User/" + userId + "/keyword");
        keywordReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keywords.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String keyword = snapshot.getValue(String.class);
                    keywords.add(keyword);
                }
                keywordAdapter.notifyDataSetChanged();
                loadFilteredNotifies();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("KeywordFragment", "Failed to read keywords", databaseError.toException());
            }
        });
    }

    private void loadFilteredNotifies() {
        notifyList.clear();
        for (NotifyDomain domain : NotifyDomain.values()) {
            for (String keyword : keywords) {
                DatabaseReference keywordNotifyReference = firebaseDatabase.getReference("KoreatechFairy4/NotifyDto/" + domain);
                DatabaseReference notifyReference = firebaseDatabase.getReference("KoreatechFairy4/User/" + userId);
                keywordNotifyReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            NotifyDto notify = snapshot.getValue(NotifyDto.class);
                            if (notify != null && (notify.getTitle().contains(keyword) || notify.getText().contains(keyword))) {
                                notify.setCount(count);
                                notifyList.add(notify);
                                notifyReference.child("keywordNotify").child("Notify_key_" + formatCount(count++)).setValue(notify);
                            }
                        }
                        keywordNotifyAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("KeywordNotifyFragment", "Failed to read notices", databaseError.toException());
                    }
                });
            }
        }

    }

    private String formatCount(int count) {
        return String.format("%02d", count);
    }

}
