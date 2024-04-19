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
        holder.tv_title.setText(notifyList.get(position).getTitle());
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
                        // 항목 클릭 이벤트 처리
                        // 예: 새 액티비티로 이동
                        /*Intent intent = new Intent(v.getContext(), 내 Activity 클래스.class);
                        intent.putExtra("item_id", position); // 추가 데이터를 전달하고자 할 때
                        v.getContext().startActivity(intent);

                         */
                    }
                }
            });

        }


    }
}
