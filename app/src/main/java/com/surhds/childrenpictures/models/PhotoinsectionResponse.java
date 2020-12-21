package com.surhds.childrenpictures.models;

import java.util.List;

public class PhotoinsectionResponse {
    private boolean error;
    private List<photoinsection> photo_info;

    public PhotoinsectionResponse(boolean error, List<photoinsection> photo_info) {
        this.error = error;
        this.photo_info = photo_info;
    }

    public boolean isError() {
        return error;
    }

    public List<photoinsection> getPhotoinsections() {
        return photo_info;
    }
}
