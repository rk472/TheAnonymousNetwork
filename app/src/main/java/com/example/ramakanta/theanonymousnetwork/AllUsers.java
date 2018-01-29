package com.example.ramakanta.theanonymousnetwork;

/**
 * Created by daduc on 30-01-2018.
 */

public class AllUsers {
    String u_thumb_image,u_name;

    public String getU_thumb_image() {
        return u_thumb_image;
    }

    public void setU_thumb_image(String u_thumb_image) {
        this.u_thumb_image = u_thumb_image;
    }

    public String getU_name() {
        return u_name;
    }

    public AllUsers() {
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public AllUsers(String u_thumb_image) {

        this.u_thumb_image = u_thumb_image;
    }
}
