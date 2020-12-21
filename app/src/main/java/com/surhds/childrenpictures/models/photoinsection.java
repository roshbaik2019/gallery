package com.surhds.childrenpictures.models;

public class photoinsection {
    private int id;
    private String url;
    private String sectionName;
    private String views;

    public photoinsection(int id, String url, String sectionName, String views) {
        this.id = id;
        this.url = url;
        this.sectionName = sectionName;
        this.views = views;
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

    public String getViews() {
        return views;
    }
}
