package com.est.streamcorn.tmdb.models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matteo on 18/01/2018.
 */

public class TmdbTvSeriesList {
    @Expose
    private List<TmdbTvSeries> results = new ArrayList<>();

    public TmdbTvSeries getFirst(){
            return results.get(0);
    }

    public int size(){
        return results.size();
    }
}
