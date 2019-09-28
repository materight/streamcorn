package com.materight.streamcorn.scrapers.models;

import android.util.SparseArray;

/**
 * Created by Matteo on 30/01/2018.
 */

public class Season {

    private SparseArray<Episode> episodes;

    public Season() {
        episodes = new SparseArray<>();
    }

    public void putEpisode(int number, Episode episode) {
        episodes.put(number, episode);
    }

    public Episode getEpisode(int number) {
        return episodes.get(number);
    }

    public SparseArray<Episode> getEpisodes() {
        return episodes;
    }

    public boolean containsEpisode(int number) {
        return episodes.get(number, null) != null;
    }
}
