package com.example.koreatechfairy4.dto;


public class GradeDto {
    double majorGrade;
    double avgGrade;
    int totalGrade; // 총 취득 학점

    public GradeDto() {}

    public double getMajorGrade() {
        return majorGrade;
    }

    public double getAvgGrade() {
        return avgGrade;
    }

    public int getTotalGrade() {
        return totalGrade;
    }

    public GradeDto(double majorGrade, double avgGrade, int totalGrade) {
        this.majorGrade = majorGrade;
        this.avgGrade = avgGrade;
        this.totalGrade = totalGrade;
    }
}
