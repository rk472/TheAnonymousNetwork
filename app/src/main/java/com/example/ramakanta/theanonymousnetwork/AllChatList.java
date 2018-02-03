package com.example.ramakanta.theanonymousnetwork;

/**
 * Created by daduc on 03-02-2018.
 */

public class AllChatList {
    String from;
    String lastMessage;
    long time;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public AllChatList(String from, String lastMessage, long time) {

        this.from = from;
        this.lastMessage = lastMessage;
        this.time = time;
    }

    public AllChatList() {

    }
}
