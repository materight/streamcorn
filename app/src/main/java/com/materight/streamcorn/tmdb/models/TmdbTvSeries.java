package com.materight.streamcorn.tmdb.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by Matteo on 18/01/2018.
 */

public class TmdbTvSeries extends TmdbMedia {

    @Expose
    @SerializedName("first_air_date")
    private Date firstAirDate;

    @Expose
    @SerializedName("last_air_date")
    private Date lastAirDate;

    @Expose
    @SerializedName("number_of_seasons")
    private int seasonNumber;

    @Expose
    @SerializedName("seasons")
    private List<TmdbSeason> seasons;

    public int getFirstAirYear() {
        if (firstAirDate == null || firstAirDate.getYear() == 0)
            return 0;
        return firstAirDate.getYear() + 1900;
    }

    public int getLastAirYear() {
        if (lastAirDate == null || lastAirDate.getYear() == 0)
            return 0;
        return lastAirDate.getYear() + 1900;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }
}
