package com.est.streamcorn.scrapers.models;

import android.util.SparseArray;

import java.util.ArrayList;

public class TvSeries implements StreamUrlContainer {

    private SparseArray<Season> seasons;

    public TvSeries() {
        this.seasons = new SparseArray<>();
    }
    
    public void putSeason(int number, Season season) {
        seasons.put(number, season);
    }

    public Season valueAt(int index) {
        return seasons.valueAt(index);
    }

    public int keyAt(int index) {
        return seasons.keyAt(index);
    }

    public Season getSeason(int key) {
        return seasons.get(key);
    }

    public SparseArray<Season> getSeasons() {
        return seasons;
    }

    public boolean containsSeason(int number) {
        return seasons.get(number, null) != null;
    }

    public void putStreamUrls(int seasonNumber, int episodeNumber, ArrayList<StreamUrl> urls) {
        Season season;
        if (containsSeason(seasonNumber)) {
            season = getSeason(seasonNumber);
        } else {
            season = new Season();
            putSeason(seasonNumber, season);
        }

        Episode episode;
        if (season.containsEpisode(episodeNumber)) {
            episode = season.getEpisode(episodeNumber);
        } else {
            episode = new Episode();
            season.putEpisode(episodeNumber, episode);
        }
        for (StreamUrl url : urls) {
            episode.addUrl(url);
        }
    }

}
