package com.example.koreatechfairy4.dto;

import java.util.Objects;

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
    private boolean isClicked = false;

    public LectureDto() {}
    public LectureDto(String code, String name, String classes, String domain, int credit, String department, String grade, String professor, String time, String registerDepartment) {
        this.code = code;
        this.name = name.replaceAll("/", ",");
        this.classes = classes;
        this.domain = domain;
        this.credit = credit;
        this.department = department;
        this.grade = grade;
        this.professor = professor;
        this.time = time;
        this.registerDepartment = registerDepartment;
    }


    @Override
    public String toString() {
        return "LectureDto{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", classes='" + classes + '\'' +
                ", domain='" + domain + '\'' +
                ", credit=" + credit +
                ", department='" + department + '\'' +
                ", grade='" + grade + '\'' +
                ", professor='" + professor + '\'' +
                ", time='" + time + '\'' +
                ", registerDepartment='" + registerDepartment + '\'' +
                '}' + '\n';
    }

    public boolean isClicked() {
        return isClicked;
    }

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

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setRegisterDepartment(String registerDepartment) {
        this.registerDepartment = registerDepartment;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public void switchIsClicked() {
        if(isClicked) isClicked = false;
        else isClicked = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LectureDto that = (LectureDto) o;
        return credit == that.credit && Objects.equals(code, that.code) && Objects.equals(name, that.name) && Objects.equals(classes, that.classes) && Objects.equals(domain, that.domain) && Objects.equals(department, that.department) && Objects.equals(grade, that.grade) && Objects.equals(professor, that.professor) && Objects.equals(time, that.time) && Objects.equals(registerDepartment, that.registerDepartment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, classes, domain, credit, department, grade, professor, time, registerDepartment);
    }
}
