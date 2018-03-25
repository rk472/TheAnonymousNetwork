package com.example.ramakanta.theanonymousnetwork;

/**
 * Created by daduc on 25-03-2018.
 */

public class DailyAttendance {
    String attendance,time;

    public DailyAttendance() {
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public DailyAttendance(String attendance, String time) {

        this.attendance = attendance;
        this.time = time;
    }
}
