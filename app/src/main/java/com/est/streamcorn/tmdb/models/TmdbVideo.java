package com.est.streamcorn.tmdb.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TmdbVideo {
    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("key")
    private String key;

    public String getKey() {
        return key;
    }
}
