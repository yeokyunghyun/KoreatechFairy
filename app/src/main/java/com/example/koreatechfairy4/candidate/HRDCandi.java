package com.example.koreatechfairy4.candidate;

import com.example.koreatechfairy4.dto.LectureDto;

import java.util.ArrayList;
import java.util.List;

public class HRDCandi {
    private List<List<LectureDto>> HRDList;

    public HRDCandi() {
        HRDList = new ArrayList<>();
    }

    public List<List<LectureDto>> getHRDList() {
        return HRDList;
    }

    public void setHRDList(List<List<LectureDto>> HRDList) {
        this.HRDList = HRDList;
    }

    public void clear() {
        HRDList.clear();
    }
}
