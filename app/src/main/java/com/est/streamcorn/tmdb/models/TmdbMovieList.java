package com.est.streamcorn.tmdb.models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matteo on 18/01/2018.
 */

public class TmdbMovieList {
    @Expose
    private List<TmdbMovie> results = new ArrayList<>();

    public TmdbMovie getFirst(){
            return results.get(0);
    }

    public int size(){
        return results.size();
    }
}
