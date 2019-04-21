package com.est.streamcorn.tmdb.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matteo on 24/01/2018.
 */

public class TmdbVideoList {
    @Expose
    @SerializedName("results")
    private List<TmdbVideo> results = new ArrayList<>();

    public TmdbVideo getFirst() {
        return results.get(0);
    }

    public int size() {
        return results.size();
    }
}
