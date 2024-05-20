package com.example.koreatechfairy4.util;

import java.util.List;

public class DayAndTimes {
    private String days;
    private List<String> timeList;
    public DayAndTimes(String days, List<String> timeList) {
        this.days = days;
        this.timeList = timeList;
    }

    public String getDays() {
        return days;
    }

    public List<String> getTimeList() {
        return timeList;
    }
}
