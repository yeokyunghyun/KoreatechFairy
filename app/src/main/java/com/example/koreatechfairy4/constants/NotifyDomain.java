package com.example.koreatechfairy4.constants;

public enum NotifyDomain {
    COMMON(14),
    BENEFIT(15),
    ACADEMIC(16),
    EMPLOY(150);
    //TRAIN(151),
    //VOLUN(191),
    //DORMI(196);


    private int link;

    NotifyDomain(int link) {
        this.link = link;
    }

    public int link() {
        return link;
    }
}
