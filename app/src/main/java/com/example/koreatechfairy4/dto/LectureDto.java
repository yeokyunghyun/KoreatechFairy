package com.example.koreatechfairy4.dto;

public class LectureDto {
    private String code; //과목코드
    private String name; //교과목명
    private String classes; //분반 class가 안됨
    private String domain; //대표이수구분
    private int credit; //학점
    private String department; //개설학부
    private String grade; //학년
    private String professor; // 교수
    private String time; // 강의가능시간
    private String registerDepartment; // 가능 학번

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getClasses() {
        return classes;
    }

    public String getDomain() {
        return domain;
    }

    public int getCredit() {
        return credit;
    }

    public String getDepartment() {
        return department;
    }

    public String getGrade() {
        return grade;
    }

    public String getProfessor() {
        return professor;
    }

    public String getTime() {
        return time;
    }

    public String getRegisterDepartment() {
        return registerDepartment;
    }
}
