package com.example.koreatechfairy4.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koreatechfairy4.R;
import com.example.koreatechfairy4.dto.LectureDto;

import java.util.ArrayList;
import java.util.List;

public class SearchLectureAdapter extends RecyclerView.Adapter<SearchLectureAdapter.LectureViewHolder> {
    private List<LectureDto> lectureList;
    private List<LectureDto> filteredLectureList;
    private OnItemClickListener listener;

    // 아이템 클릭 리스너 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(LectureDto lecture);
    }

    public SearchLectureAdapter(List<LectureDto> lectureList, OnItemClickListener listener) {
        this.lectureList = lectureList;
        this.filteredLectureList = new ArrayList<>(lectureList);
        this.listener = listener;
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
        LectureDto lecture = filteredLectureList.get(position);
        holder.code.setText(lecture.getCode());
        holder.name.setText(lecture.getName());
        holder.classes.setText(lecture.getClasses());
        holder.professor.setText(lecture.getProfessor());
        holder.credit.setText("" + lecture.getCredit());
        holder.domain.setText(lecture.getDomain());
        holder.bind(lecture, listener);
    }

    @Override
    public int getItemCount() {
        return filteredLectureList.size();
    }

    public void filter(String query) {
        filteredLectureList.clear();
        if (query.isEmpty()) {
            filteredLectureList.addAll(lectureList);
        } else {
            for (LectureDto lecture : lectureList) {
                if (lecture.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredLectureList.add(lecture);
                }
            }
        }
        notifyDataSetChanged();
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

        public void bind(final LectureDto lecture, final OnItemClickListener listener) {
            code.setText(lecture.getCode());
            name.setText(lecture.getName());
            classes.setText(lecture.getClasses());
            professor.setText(lecture.getProfessor());
            credit.setText(String.valueOf(lecture.getCredit()));  // 학점은 int이므로 String으로 변환
            domain.setText(lecture.getDomain());
            itemView.setOnClickListener(v -> listener.onItemClick(lecture));
        }
    }
}