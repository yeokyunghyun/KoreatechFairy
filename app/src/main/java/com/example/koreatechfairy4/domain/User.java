package com.example.koreatechfairy4.domain;

//사용자 계정 정보 모델 클래스
public class User {

    private UserId id;
    private String name;
    private String password;
    private String major;
    private String studentId;


    public User() {} //빈 생성자 필수

    public User(String id, String name, String registerPw, String selectedMajor, String selectedStudentId) {
        this.id = new UserId(id); // 아이디 로직 추가
        this.name = name;
        this.password = registerPw;
        this.major = selectedMajor;
        this.studentId = selectedStudentId;
    }

    public String getName() {return name;}

    public String getId() {
        return id.getId();
    }

    public String getPassword() {
        return password;
    }

    public String getMajor() {
        return major;
    }

    public String getStudentId() {
        return studentId;
    }
}
