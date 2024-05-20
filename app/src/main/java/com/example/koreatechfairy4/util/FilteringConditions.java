package com.example.koreatechfairy4.util;

public class FilteringConditions {
    private String grade;
    private String major;
    private String concentration; //세부전공
    private int majorCredit, generalCredit, MSCCredit, HRDCredit;

    public FilteringConditions() {}

    public FilteringConditions(String grade, String major, String concentration, int majorCredit,
                               int generalCredit, int MSCCredit, int HRDCredit) {
        this.grade = grade;
        this.major = major;
        this.concentration = concentration;
        this.majorCredit = majorCredit;
        this.generalCredit = generalCredit;
        this.MSCCredit = MSCCredit;
        this.HRDCredit = HRDCredit;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    public int getMajorCredit() {
        return majorCredit;
    }

    public void setMajorCredit(int majorCredit) {
        this.majorCredit = majorCredit;
    }

    public int getMSCCredit() {
        return MSCCredit;
    }

    public void setMSCCredit(int MSCCredit) {
        this.MSCCredit = MSCCredit;
    }

    public int getHRDCredit() {
        return HRDCredit;
    }

    public void setHRDCredit(int HRDCredit) {
        this.HRDCredit = HRDCredit;
    }

    public int getGeneralCredit() {
        return generalCredit;
    }

    public void setGeneralCredit(int generalCredit) {
        this.generalCredit = generalCredit;
    }
}
