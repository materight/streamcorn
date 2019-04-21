package com.est.streamcorn.models;

import com.est.streamcorn.tmdb.models.TmdbMovie;

import java.util.ArrayList;

public class Movie implements StreamUrlContainer {
    private ArrayList<StreamUrl> urls;
    private String overview;

    public Movie() {
        urls = new ArrayList<>();
        overview = "";
    }

    public void addStreamUrl(StreamUrl streamUrl) {
        urls.add(streamUrl);
    }

    public StreamUrl getStreamUrl(int index) {
        return urls.get(index);
    }

    public ArrayList<StreamUrl> getUrls() {
        return urls;
    }

    public void setTmdbData(TmdbMovie tmdbMovie) {
        if (overview.isEmpty())
            overview = tmdbMovie.getOverview();
    }

    public String getOverview() {
        return overview;
    }


}
