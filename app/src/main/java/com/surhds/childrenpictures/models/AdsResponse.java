package com.surhds.childrenpictures.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdsResponse {
    @SerializedName("error")
    private boolean error;

    private List<Ads> ads;

    public AdsResponse(boolean error, List<Ads> ads) {
        this.error = error;
        this.ads = ads;
    }

    public boolean isError() {
        return error;
    }

    public List<Ads> getAds() {
        return ads;
    }
}
