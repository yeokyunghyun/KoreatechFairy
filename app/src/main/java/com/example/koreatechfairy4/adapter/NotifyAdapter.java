package com.example.koreatechfairy4.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koreatechfairy4.DetailNotifyActivity;
import com.example.koreatechfairy4.R;
import com.example.koreatechfairy4.dto.NotifyDto;

import java.util.ArrayList;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.NotifyViewHolder> {

    private ArrayList<NotifyDto> notifyList;
    private Context context;
    private int count = 1;

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

    public class NotifyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        TextView tv_num;
        TextView tv_date;

        public NotifyViewHolder(@NonNull View itemView) {
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
                        //intent.putExtra("type", item.getDomain());  // 공지사항의 유형을 추가
                        intent.putExtra("title", item.getTitle());
                        intent.putExtra("date", item.getDate());
                        //intent.putExtra("content", item.getText());  // 내용도 추가할 수 있음
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
