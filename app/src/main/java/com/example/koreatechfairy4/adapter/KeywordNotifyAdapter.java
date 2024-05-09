package com.example.koreatechfairy4.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koreatechfairy4.DetailNotifyActivity;
import com.example.koreatechfairy4.R;
import com.example.koreatechfairy4.dto.NotifyDto;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class KeywordNotifyAdapter extends RecyclerView.Adapter<KeywordNotifyAdapter.KeywordNotifyViewHolder>{

    private ArrayList<NotifyDto> notifyList;
    private Context context;
    private int count = 1;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private String userId;

    public KeywordNotifyAdapter(ArrayList<NotifyDto> notifyList, Context context, String userId) {
        this.notifyList = notifyList;
        this.context = context;
        this.userId = userId;
    }


    @NonNull
    @Override
    public KeywordNotifyAdapter.KeywordNotifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_keyword_notify, parent, false);
        KeywordNotifyAdapter.KeywordNotifyViewHolder holder = new KeywordNotifyAdapter.KeywordNotifyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordNotifyAdapter.KeywordNotifyViewHolder holder, int position) {
        holder.tv_title.setText(notifyList.get(position).getTitle());
        if (notifyList.get(position).getNotifyNum() == 0)
            holder.tv_num.setText(String.valueOf(count++));
        else
            holder.tv_num.setText(String.valueOf(notifyList.get(position).getNotifyNum()));
        holder.tv_date.setText(notifyList.get(position).getDate());


    }

    @Override
    public int getItemViewType(int position) {
        return notifyList.get(position).getDomain();
    }

    @Override
    public int getItemCount() {
        return (notifyList != null ? notifyList.size() : 0);
    }

    private void removeItem(int position) {
        NotifyDto item = notifyList.get(position);
        if (item.getCount() != 0) { // Ensure there is a key
            DatabaseReference ref = firebaseDatabase.getReference("KoreatechFairy4/User/" + userId + "/keywordNotify");
            ref.child("Notify_key_" + formatCount(item.getCount())).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Remove from the local list and update the adapter
                        notifyList.remove(position);
                        notifyDataSetChanged();
                        Log.d("Adapter", "Item deleted successfully");
                    })
                    .addOnFailureListener(e -> Log.d("Adapter", "Failed to delete item", e));
        }
    }

    private String formatCount(int count) {
        return String.format("%02d", count);
    }


    public class KeywordNotifyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        TextView tv_num;
        TextView tv_date;
        ImageButton deleteNotify;

        public KeywordNotifyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_num = itemView.findViewById(R.id.tv_num);
            this.tv_date = itemView.findViewById(R.id.tv_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        NotifyDto item = notifyList.get(position);
                        Intent intent = new Intent(v.getContext(), DetailNotifyActivity.class);
                        intent.putExtra("type", item.getDomain());  // 공지사항의 유형을 추가
                        intent.putExtra("title", item.getTitle());
                        intent.putExtra("date", item.getDate());
                        intent.putExtra("content", item.getText());  // 내용도 추가할 수 있음
                        intent.putExtra("html", item.getHtml());
                        intent.putExtra("imgUrls", item.getImgUrls());
                        intent.putExtra("baseUrl", item.getBaseUrl());
                        intent.putExtra("author", item.getAuthor());
                        v.getContext().startActivity(intent);
                    }
                }
            });

        }
    }
}
