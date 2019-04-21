package com.est.streamcorn.models;

import com.est.streamcorn.models.Media;
import com.est.streamcorn.tmdb.models.TmdbMedia;
import com.est.streamcorn.tmdb.models.TmdbMovie;
import com.est.streamcorn.tmdb.models.TmdbTvSeries;

/**
 * Created by Matteo on 14/03/2018.
 */

public class MediaContainer {
    private Media media;
    private StreamUrlContainer streamUrlContainer;
    private TmdbMedia tmdbMedia;

    @Media.MediaType
    private int type;

    public MediaContainer(Media media, Movie movie, TmdbMovie tmdbMovie){
        this.media = media;
        this.streamUrlContainer = movie;
        this.tmdbMedia = tmdbMovie;
        this.type = Media.MOVIE;
    }

    public MediaContainer(Media media, TvSeries tvSeries, TmdbTvSeries tmdbTvSeries){
        this.media = media;
        this.streamUrlContainer = tvSeries;
        this.tmdbMedia = tmdbTvSeries;
        this.type = Media.TV_SERIES;
    }

    public Media getMedia() {
        return media;
    }

    public StreamUrlContainer getStreamUrlContainer() {
        return streamUrlContainer;
    }

    public TmdbMedia getTmdbMedia() {
        return tmdbMedia;
    }

    public boolean equals(MediaContainer mediaContainer) {
        return this.media.getUrl().equals(mediaContainer.media.getUrl());
    }
}
