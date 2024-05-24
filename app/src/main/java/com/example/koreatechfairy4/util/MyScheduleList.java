package com.example.koreatechfairy4.util;

import com.example.koreatechfairy4.dto.LectureDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyScheduleList {

    // 싱글톤 인스턴스
    private static MyScheduleList instance;

    // 멤버 변수
    private List<LectureDto> lectureList;
    private HashMap<String, List<String>> timeTable;

    // private 생성자
    private MyScheduleList() {
        lectureList = new ArrayList<>();
        timeTable = new HashMap<>();
    }

    // 인스턴스를 반환하는 메서드
    public static synchronized MyScheduleList getInstance() {
        if (instance == null) {
            instance = new MyScheduleList();
        }
        return instance;
    }

    // 멤버 변수에 대한 getter
    public List<LectureDto> getLectureList() {
        return lectureList;
    }

    public HashMap<String, List<String>> getTimeTable() {
        return timeTable;
    }

    public boolean isDuplicateName(String name) {
        for(LectureDto l : lectureList) {
            if(l.getName().equals(name)) return true;
        }
        return false;
    }

    public boolean isDuplicateTime(List<DayAndTimes> dayAndTimes) {
        for(DayAndTimes dat : dayAndTimes) {
            if(timeTable.containsKey(dat.getDays())) {
                for (String time : dat.getTimeList()) {
                    if (timeTable.get(dat.getDays()).contains(time)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void addTime(String day, String time) {
        timeTable.putIfAbsent(day, new ArrayList<>());
        timeTable.get(day).add(time);
    }

    public void addLecture(LectureDto lecture) {
        lectureList.add(lecture);
    }
    public void addAllLecture(List<LectureDto> lectures) {
        lectureList.addAll(lectures);
    }

    public void removeLecture(LectureDto lecture) {
        lectureList.remove(lecture);
    }

    public void removeTime(List<DayAndTimes> dayAndTimes) {
        for(DayAndTimes dat : dayAndTimes) {
            String day = dat.getDays();
            for(String time : dat.getTimeList()) {
                timeTable.get(day).remove(time);
            }
        }
    }

    public void clearAll() {
        lectureList.clear();
        timeTable.clear();
    }
}
