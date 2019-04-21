package com.est.streamcorn.tmdb.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Matteo on 14/03/2018.
 */

public class TmdbMedia {
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("overview")
    private String overview;


    @Expose
    @SerializedName("vote_average")
    private float vote;

    @Expose
    @SerializedName("backdrop_path")
    private String backdropPath;

    @Expose
    @SerializedName("videos")
    private TmdbVideoList videos;

    public int getId() {
        return id;
    }

    public String getOverview() {
        return overview;
    }

    public float getVote() {
        return vote;
    }

    public String getBackdropPath() {
        return "http://image.tmdb.org/t/p/w780" + backdropPath;
    }

    public TmdbVideoList getVideos() {
        return videos;
    }
}
