package com.example.ramakanta.theanonymousnetwork;

/**
 * Created by daduc on 26-03-2018.
 */

public class MonthlyAttendance {
    long total,att;
    String subject;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getAtt() {
        return att;
    }

    public void setAtt(long att) {
        this.att = att;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public MonthlyAttendance(long total, long att, String subject) {

        this.total = total;
        this.att = att;
        this.subject = subject;
    }

    public MonthlyAttendance() {
    }
}
