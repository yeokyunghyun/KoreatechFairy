package com.example.koreatechfairy4.domain;

public class UserId {
    private String id;

    public UserId(String id) {
        if (!isValidId(id)) {
            throw new IllegalArgumentException("아이디형식이 맞지 않습니다. (숫자 영문 포함 10글자 이내)");
        }
        this.id = id;
    }

    public String getId() {
        return id;
    }

    private boolean isValidId(String id) {
        // ID 유효성 검사 로직 (예: 길이, 특수 문자 포함 등)
        return id.matches("^(?=.*[a-zA-Z])(?=.*[0-9]).{1,10}$");
    }

    @Override
    public String toString() {
        return id;
    }
}
