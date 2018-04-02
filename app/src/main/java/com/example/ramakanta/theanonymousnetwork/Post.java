package com.example.ramakanta.theanonymousnetwork;

public class Post {
    String p_image,p_user,p_caption;
    int p_like;
    long p_time;


    public Post(String p_image, String p_user, String p_caption, int p_like, long p_time) {
        this.p_image = p_image;
        this.p_user = p_user;
        this.p_caption = p_caption;
        this.p_like = p_like;
        this.p_time = p_time;
    }
    public Post() {
    }

    public String getP_image() {
        return p_image;
    }

    public void setP_image(String p_image) {
        this.p_image = p_image;
    }

    public String getP_user() {
        return p_user;
    }

    public void setP_user(String p_user) {
        this.p_user = p_user;
    }

    public String getP_caption() {
        return p_caption;
    }

    public void setP_caption(String p_caption) {
        this.p_caption = p_caption;
    }

    public int getP_like() {
        return p_like;
    }

    public void setP_like(int p_like) {
        this.p_like = p_like;
    }

    public long getP_time() {
        return p_time;
    }

    public void setP_time(long p_time) {
        this.p_time = p_time;
    }
}
