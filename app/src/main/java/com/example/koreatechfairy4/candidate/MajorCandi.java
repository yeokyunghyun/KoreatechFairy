package com.example.koreatechfairy4.candidate;

import com.example.koreatechfairy4.dto.LectureDto;

import java.util.ArrayList;
import java.util.List;

public class MajorCandi {
    private List<List<LectureDto>> majorList;

    public MajorCandi() {
        this.majorList = new ArrayList<>();
    }

    public List<List<LectureDto>> getMajorList() {
        return majorList;
    }

    public void setMajorList(List<List<LectureDto>> majorList) {
        this.majorList = majorList;
    }

    public void clear() {
        majorList.clear();
    }
}
