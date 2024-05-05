package com.example.koreatechfairy4.dto;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NotifyDto {
    private String title;
    private String text;
    private int domain;
    private int notifyNum;
    private String date;
    private String author;
    private ArrayList<String> imgUrls;
    private String baseUrl;
    private String html;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

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

    @Override
    public boolean equals(@Nullable Object obj) {
        NotifyDto input = (NotifyDto) obj;
        return this.getTitle().equals(input.getTitle());
    }
}
