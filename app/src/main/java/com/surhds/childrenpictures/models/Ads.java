package com.surhds.childrenpictures.models;

public class Ads {
    private int id;
    private String code;
    private int click,views;

    public Ads(int id, String code,int click,int views) {
        this.id = id;
        this.code = code;
        this.click= click;
        this.views= views;
    }

    public int getClick() {
        return click;
    }

    public int getViews() {
        return views;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

}
