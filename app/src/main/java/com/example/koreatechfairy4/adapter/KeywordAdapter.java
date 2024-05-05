package com.example.koreatechfairy4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koreatechfairy4.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.KeywordViewHolder> {
    private Context context;
    private List<String> keywordList;
    private String userId;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public KeywordAdapter(List<String> keywordList, Context context, String userId) {
        this.context = context;
        this.keywordList = keywordList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_keyword, parent, false);
        return new KeywordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordViewHolder holder, int position) {
        String keyword = keywordList.get(position);
        holder.keywordTextView.setText(keyword);

        holder.deleteKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteKeywordFromFirebase(keyword);
            }
        });
    }

    @Override
    public int getItemCount() {
        return keywordList.size();
    }

    private void deleteKeywordFromFirebase(String keyword) {
        // 파이어베이스 데이터베이스에서 키워드 경로를 찾아서 해당 노드 삭제
        DatabaseReference databaseReference = firebaseDatabase.getReference("KoreatechFairy4/User/" + userId + "/keyword");
        databaseReference.child(keyword).removeValue();
    }

    public static class KeywordViewHolder extends RecyclerView.ViewHolder {
        TextView keywordTextView;
        ImageButton deleteKeyword;

        public KeywordViewHolder(@NonNull View itemView) {
            super(itemView);
            keywordTextView = itemView.findViewById(R.id.tv_keyword);
            deleteKeyword = itemView.findViewById(R.id.imgBtn_keyword_delete);
        }
    }
}
