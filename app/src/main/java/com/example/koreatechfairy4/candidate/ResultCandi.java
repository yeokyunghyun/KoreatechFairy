package com.example.koreatechfairy4.candidate;

import com.example.koreatechfairy4.dto.LectureDto;

import java.util.ArrayList;
import java.util.List;

public class ResultCandi {
    private List<List<LectureDto>> resultList;

    public ResultCandi() {
        this.resultList = new ArrayList<>();
    }

    public List<List<LectureDto>> getResultList() {
        return resultList;
    }

    public void setResultList(List<List<LectureDto>> resultList) {
        this.resultList = resultList;
    }

    public void clear() {
        resultList.clear();
    }

    public void add(List<LectureDto> list) {
        resultList.add(list);
    }
}
