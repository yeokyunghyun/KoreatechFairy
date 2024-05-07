package com.example.koreatechfairy4.domain;

//사용자 계정 정보 모델 클래스
public class User {
    private String userId;
    private String id;
    private String name;
    private String password;
    private String major;
    private String studentId;
    private double majorGrade;
    private double avgGrade;
    private int totalCredit;

    public User() {} //빈 생성자 필수

    public User(String userId, String id, String name, String registerPw, String selectedMajor, String selectedStudentId) {
        this.userId = userId;
        if(!isValidId(id)) throw new IllegalArgumentException("아이디형식이 맞지 않습니다. (숫자 영문 포함 10글자 이내)");
        this.id = id; // 아이디 로직 추가
        this.name = name;
        this.password = registerPw;
        this.major = selectedMajor;
        this.studentId = selectedStudentId;
        this.majorGrade = 0;
        this.avgGrade = 0;
        this.totalCredit = 0;
    }

    public String getUserId() {
        return userId;
    }
    public String getName() {return name;}

    public String getId() {
        return id;
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

    public double getMajorGrade() {
        return majorGrade;
    }

    public double getAvgGrade() {
        return avgGrade;
    }

    public int getTotalCredit() {
        return totalCredit;
    }

    private boolean isValidId(String id) {
        // ID 유효성 검사 로직 (예: 길이, 특수 문자 포함 등)
        return id.matches("^(?=.*[a-zA-Z])(?=.*[0-9]).{1,10}$");
    }
}
