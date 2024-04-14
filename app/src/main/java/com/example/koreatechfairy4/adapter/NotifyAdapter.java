package com.example.koreatechfairy4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koreatechfairy4.R;
import com.example.koreatechfairy4.dto.NotifyDto;

import java.util.ArrayList;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.NotifyViewHolder> {

    private ArrayList<NotifyDto> notifyList;
    private Context context;

    public NotifyAdapter(ArrayList<NotifyDto> notifyList, Context context) {
        this.notifyList = notifyList;
        this.context = context;
    }

    @NonNull
    @Override
    public NotifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_notify, parent, false);
        NotifyViewHolder holder = new NotifyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotifyViewHolder holder, int position) {

        //holder.tv_title.setText(notifyList.get(position).getTitle());
        holder.tv_title.setText("hitext");
        //holder.tv_text.setText(notifyList.get(position).getText());
        //holder.tv_notifyNum.setText(String.valueOf(notifyList.get(position).getNotifyNum()));

    }

    @Override
    public int getItemCount() {
        return (notifyList != null ? notifyList.size() : 0);
    }

    public class NotifyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        TextView tv_text;
        TextView tv_notifyNum;

        public NotifyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_text = itemView.findViewById(R.id.tv_text);
            this.tv_notifyNum = itemView.findViewById(R.id.tv_notifyNum);
        }
    }
}
