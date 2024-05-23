package com.example.koreatechfairy4.util;

import androidx.annotation.Nullable;

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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null | getClass() != obj.getClass()) return false;
        DayAndTimes o = (DayAndTimes) obj;
        return days.equals(o.days) && timeList.equals(o.timeList);
    }
}
