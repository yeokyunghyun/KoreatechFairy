package com.example.koreatechfairy4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koreatechfairy4.R;

import java.util.List;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.KeywordViewHolder> {
    private Context context;
    private List<String> keywordList;

    public KeywordAdapter(List<String> keywordList, Context context) {
        this.context = context;
        this.keywordList = keywordList;
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
    }

    @Override
    public int getItemCount() {
        return keywordList.size();
    }

    public static class KeywordViewHolder extends RecyclerView.ViewHolder {
        TextView keywordTextView;

        public KeywordViewHolder(@NonNull View itemView) {
            super(itemView);
            keywordTextView = itemView.findViewById(R.id.tv_keyword);
        }
    }
}
