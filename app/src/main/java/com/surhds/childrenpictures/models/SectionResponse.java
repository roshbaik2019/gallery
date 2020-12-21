package com.surhds.childrenpictures.models;

import java.util.List;

public class SectionResponse {

    private boolean error;
    private List<section> section;

    public SectionResponse(boolean error, List<section> section) {
        this.error = error;
        this.section = section;
    }

    public boolean isError() {
        return error;
    }

    public List<section> getSectionList() {
        return section;
    }
}
