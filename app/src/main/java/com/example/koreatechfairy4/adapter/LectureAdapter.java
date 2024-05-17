package com.example.koreatechfairy4.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koreatechfairy4.R;
import com.example.koreatechfairy4.dto.LectureDto;
import java.util.List;

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.LectureViewHolder> {
    private List<LectureDto> lectureList;

    public LectureAdapter(List<LectureDto> lectureList) {
        this.lectureList = lectureList;
    }

    @NonNull
    @Override
    public LectureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lecture, parent, false);
        return new LectureViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LectureViewHolder holder, int position) {
        LectureDto lecture = lectureList.get(position);
        holder.code.setText(lecture.getCode());
        holder.name.setText(lecture.getName());
        holder.classes.setText(lecture.getClasses());
        holder.professor.setText(lecture.getProfessor());
        holder.credit.setText("" + lecture.getCredit());
        holder.domain.setText(lecture.getDomain());
    }

    @Override
    public int getItemCount() {
        return lectureList.size();
    }

    public static class LectureViewHolder extends RecyclerView.ViewHolder {
        public TextView code;
        public TextView name;
        public TextView classes;
        public TextView professor;
        public TextView credit;
        public TextView domain;

        public LectureViewHolder(@NonNull View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.code);
            name = itemView.findViewById(R.id.name);
            classes = itemView.findViewById(R.id.classes);
            professor = itemView.findViewById(R.id.professor);
            credit = itemView.findViewById(R.id.credit);
            domain = itemView.findViewById(R.id.domain);
        }
    }
}