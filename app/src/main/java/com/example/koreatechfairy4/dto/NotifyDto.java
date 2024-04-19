package com.example.koreatechfairy4.dto;

import java.util.ArrayList;

public class NotifyDto {
    private String title;
    private String text;
    private int domain;
    private int notifyNum;
    private String date;
    private String author;
    private ArrayList<String> imgUrls;

    public NotifyDto() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setImgUrls(ArrayList<String> imageUrls) {
        imgUrls = imageUrls;
    }

    public ArrayList<String> getImgUrls() {
        return imgUrls;
    }
}
