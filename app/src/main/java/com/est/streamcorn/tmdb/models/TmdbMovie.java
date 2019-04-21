package com.est.streamcorn.tmdb.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Matteo on 18/01/2018.
 */

public class TmdbMovie extends TmdbMedia{

    @Expose
    @SerializedName("release_date")
    private Date releaseDate;

    @Expose
    @SerializedName("runtime")
    private int duration;

    public int getReleaseYear() {
        if(releaseDate == null || releaseDate.getYear() == 0)
            return 0;
        return releaseDate.getYear() + 1900;
    }

    public int getDuration() {
        return duration;
    }
}
