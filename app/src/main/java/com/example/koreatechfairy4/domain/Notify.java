package com.example.koreatechfairy4.domain;

public class Notify {
    private String title;   //제목
    private String text;    //내용
    private int domain;     //구분(일반, 학사, 장학 등)
    private int notifyNum;  //공지사항번호

    public Notify() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getDomain() {
        return domain;
    }

    public void setDomain(int domain) {
        this.domain = domain;
    }

    public int getNotifyNum() {
        return notifyNum;
    }

    public void setNotifyNum(int notifyNum) {
        this.notifyNum = notifyNum;
    }
}
