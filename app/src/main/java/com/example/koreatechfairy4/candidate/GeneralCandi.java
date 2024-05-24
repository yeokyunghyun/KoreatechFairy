package com.example.koreatechfairy4.candidate;

import com.example.koreatechfairy4.dto.LectureDto;

import java.util.ArrayList;
import java.util.List;

public class GeneralCandi {
    private List<List<LectureDto>> generalList;

    public GeneralCandi() {
        generalList = new ArrayList<>();
    }

    public List<List<LectureDto>> getGeneralList() {
        return generalList;
    }

    public void setGeneralList(List<List<LectureDto>> generalList) {
        this.generalList = generalList;
    }

    public void clear() {
        generalList.clear();
    }
}
