package com.example.koreatechfairy4.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koreatechfairy4.R;
import com.example.koreatechfairy4.adapter.NotifyAdapter;
import com.example.koreatechfairy4.constants.NotifyDomain;
import com.example.koreatechfairy4.dto.NotifyDto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommonNotifyFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotifyAdapter adapter;
    private ArrayList<NotifyDto> notifyList;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();


    public CommonNotifyFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common_notify, container, false);
        recyclerView = view.findViewById(R.id.notify_common);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notifyList = new ArrayList<>();
        adapter = new NotifyAdapter(notifyList, getContext());

        loadNotifyData();

        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadNotifyData() {
        DatabaseReference databaseReference = firebaseDatabase.getReference("KoreatechFairy4/NotifyDto/commonNotify");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notifyList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NotifyDto notifyDto = dataSnapshot.getValue(NotifyDto.class);
                    notifyList.add(notifyDto);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotifyActivity", String.valueOf(error.toException()));
            }
        });
    }


}
