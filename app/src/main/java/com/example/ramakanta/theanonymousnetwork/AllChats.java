package com.example.ramakanta.theanonymousnetwork;

/**
 * Created by daduc on 03-02-2018.
 */

public class AllChats {
    private String message;
    private long time;
    private String from;


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }



    public AllChats() {
    }

    public AllChats(String message, long time, String from) {
        this.message = message;
        this.time = time;
        this.from = from;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
