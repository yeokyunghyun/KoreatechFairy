package com.example.koreatechfairy4.constants;

public enum MajorDomain {

    기계공학부("기계"),
    메카트로닉스공학부("메카"),
    전기전자통신공학부("전기"),
    컴퓨터공학부("컴퓨터"),
    디자인건축공학부("디자인"),
    에너지신소재화학공학부("에너지"),
    산업경영학부("산업"),
    고용서비스정책학과("고용");


    private String major;
    MajorDomain(String major) {
        this.major = major;
    }

    public String major() {
        return major;
    }
}
