package com.example.koreatechfairy4.domain;

public class Lecture {
    public Lecture() {}

    private String lectureName = null; // 강의명 ex) 데이터베이스 설계
    private String domain = null; // 이수구분 ex) 학부 공통 선택
    private int credit = 0; // 학점 ex) 4
    private String prof = null; // 교수 ex) 김은경

    public String getLectureName() {
        return lectureName;
    }

    public String getDomain() {
        return domain;
    }

    public int getCredit() {
        return credit;
    }

    public String getProf() {
        return prof;
    }
    public static class Builder {
        private String lectureName = null;
        private String domain = null; // 이수구분
        private int credit = 0;
        private String prof = null;

        public Builder() {}
        public Builder lectureName(String lectureName) {
            this.lectureName = lectureName;
            return this;
        }

        public Builder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder credit(int credit) {
            this.credit = credit;
            return this;
        }

        public Builder prof(String prof) {
            this.prof = prof;
            return this;
        }

        public Lecture build() {
            return new Lecture(this);
        }

    }

    private Lecture(Builder builder) {
        this.lectureName = builder.lectureName;
        this.domain = builder.domain;
        this.credit = builder.credit;
        this.prof = builder.prof;
    }

    @Override
    public String toString() {
        return "[강의명: " + lectureName +
                ", 교수: " + prof +
                ", 이수구분: " + domain +
                ", 학점: " + credit + "]";
    }
}
