package com.example.ramakanta.theanonymousnetwork;

/**
 * Created by daduc on 15-03-2018.
 */

public class TimeTable {
    String name,time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public TimeTable(String name, String time) {

        this.name = name;
        this.time = time;
    }

    public TimeTable() {

    }
}
