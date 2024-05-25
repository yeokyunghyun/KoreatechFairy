package com.example.koreatechfairy4.constants;

public enum MajorLink {
    기계공학부("https://www.koreatech.ac.kr/menu.es?mid=a70302000000"),
    메카트로닉스공학부("https://www.koreatech.ac.kr/menu.es?mid=a80302010200"),
    전기전자통신공학부("https://www.koreatech.ac.kr/menu.es?mid=a90302010200"),
    컴퓨터공학부("https://www.koreatech.ac.kr/menu.es?mid=b10403000000"),
    디자인공학부("https://www.koreatech.ac.kr/menu.es?mid=b20302000000"),
    건축공학부("https://www.koreatech.ac.kr/menu.es?mid=b30202000000"),
    에너지신소재화학공학부("https://www.koreatech.ac.kr/menu.es?mid=b40302000000"),
    산업경영학부("https://www.koreatech.ac.kr/menu.es?mid=b60302010100"),
    고용서비스정책학과("https://www.koreatech.ac.kr/board.es?mid=b80302000000&bid=0150");

    private String link;

    MajorLink(String link) {
        this.link = link;
    }

    public String link() {
        return link;
    }
}
