package com.example.koreatechfairy4.candidate;

import com.example.koreatechfairy4.dto.LectureDto;

import java.util.ArrayList;
import java.util.List;

public class MSCCandi {
    private List<List<LectureDto>> MSCList;

    public MSCCandi() {
        MSCList = new ArrayList<>();
    }

    public List<List<LectureDto>> getMSCList() {
        return MSCList;
    }

    public void setMSCList(List<List<LectureDto>> MSCList) {
        this.MSCList = new ArrayList<>(MSCList);
    }

    public void add(List<LectureDto> lectureList) {
        MSCList.add(new ArrayList<>(lectureList));
    }

    public void clear() {
        MSCList.clear();
    }

    public boolean isEmpty() {
        return MSCList.isEmpty();
    }
}