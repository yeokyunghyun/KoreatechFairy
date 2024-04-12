package com.example.koreatechfairy4;

//사용자 계정 정보 모델 클래스
public class User {

    private String uId;
    private String email;
    private String password;
    private String major;
    private String studentId;


    public User() {} //빈 생성자 필수

    public User(String uid, String email, String registerPw, String selectedMajor, String selectedStudentId) {
        this.uId = uid;
        this.email = email;
        this.password = registerPw;
        this.major = selectedMajor;
        this.studentId = selectedStudentId;
    }

    public String getuId() {
        return uId;
    }

    public String getEmail() {
        return email;
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
