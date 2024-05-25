package com.example.koreatechfairy4.util;

import android.util.Log;

import com.example.koreatechfairy4.constants.TimeTab;
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
        Log.d("name", lecture.getName());
    }

    public boolean addAllLecture(List<LectureDto> lectures) {
        for (LectureDto lectureDto : lectures) {
            addLecture(lectureDto);
            addAllTime(changeToDayAndTimes(lectureDto.getTime()));
        }
        return true;
    }

    public void addAllTime(List<DayAndTimes> datList) {
        for (DayAndTimes dat : datList) {
            String d = dat.getDays();
            for (String t : dat.getTimeList()) {
                addTime(d, t);
            }
        }
    }

    public void removeLecture(LectureDto lecture) {
        lectureList.remove(lecture);
        Log.d("lectureName", lecture.getName());
    }

    public void removeTime(List<DayAndTimes> dayAndTimes) {
        for(DayAndTimes dat : dayAndTimes) {
            String day = dat.getDays();
            for(String time : dat.getTimeList()) {
                timeTable.get(day).remove(time);
                Log.d("time", time);
            }
        }
    }

    public void clearAll() {
        lectureList.clear();
        timeTable.clear();
    }

    private List<DayAndTimes> changeToDayAndTimes(String time) {
        String days = String.valueOf(time.charAt(0)); // 첫 요일
        String[] splitLectureTime = time.split(",");

        List<DayAndTimes> dayAndTimes = new ArrayList<>();

        for (String lectureTime : splitLectureTime) {
            if (!(lectureTime.charAt(0) >= '0' && lectureTime.charAt(0) <= '9')) {
                days = String.valueOf(lectureTime.charAt(0));
            }
            List<String> timeList = new ArrayList<>();

            // ~로 나눠서 시간 추출
            String[] splitTilde = lectureTime.split("~");
            int firstLength = splitTilde[0].length();
            String firstTime = splitTilde[0].substring(firstLength - 3, firstLength);
            String secondTime = splitTilde[1].substring(0, 3);

            boolean isChecked = false;
            for (int i = 0; i < TimeTab.timeTab.length; ++i) {
                if (firstTime.equals(TimeTab.timeTab[i][0])) {
                    isChecked = true;
                    for (int j = i; j < TimeTab.timeTab.length; ++j) {
                        timeList.add(TimeTab.timeTab[j][1]);
                        if (secondTime.equals(TimeTab.timeTab[j][0])) break;
                    }
                }
                if (isChecked) break;
            }
            if (!isChecked) {
                timeList.add("1800");
            }

            dayAndTimes.add(new DayAndTimes(days, timeList));
        }

        return dayAndTimes;
    }
}
