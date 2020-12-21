package com.surhds.childrenpictures.models;

import com.google.gson.annotations.SerializedName;

public class section {
    private int id;
    private String url;
    private String sectionName;

    public section(int id, String url, String sectionName) {

        this.id = id;
        this.url = url;
        this.sectionName = sectionName;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getSectionName() {
        return sectionName;
    }
}
