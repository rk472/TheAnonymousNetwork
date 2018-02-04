package com.example.ramakanta.theanonymousnetwork;

/**
 * Created by daduc on 05-02-2018.
 */

public class Storage {
    String url;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Storage(String url, String name) {

        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    public Storage() {
    }
}
