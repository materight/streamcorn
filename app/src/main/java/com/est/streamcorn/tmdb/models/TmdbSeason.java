package com.est.streamcorn.tmdb.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by Matteo on 29/01/2018.
 */

public class TmdbSeason {
    @Expose
    @SerializedName("air_date")
    private Date airDate;

    @Expose
    @SerializedName("season_number")
    private int number;

    @Expose
    @SerializedName("overview")
    private String overview;

    @Expose
    @SerializedName("episodes")
    private List<TmdbEpisode> episodes;

    public int getNumber() {
        return number;
    }

    public List<TmdbEpisode> getEpisodes() {
        return episodes;
    }
}
