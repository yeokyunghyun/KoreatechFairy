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


    public LectureDto(String code, String name, String classes, String domain, int credit, String department, String grade, String professor, String time, String registerDepartment) {
        this.code = code;
        this.name = name;
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
                '}';
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
}
