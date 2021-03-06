package com.materight.streamcorn.scrapers.models;

import com.materight.streamcorn.tmdb.models.TmdbMedia;
import com.materight.streamcorn.tmdb.models.TmdbMovie;
import com.materight.streamcorn.tmdb.models.TmdbTvSeries;

public class MediaContainer {
    private Media media;
    private StreamUrlContainer streamUrlContainer;
    private TmdbMedia tmdbMedia;

    @MediaType
    private int type;

    public MediaContainer(Media media, Movie movie, TmdbMovie tmdbMovie) {
        this.media = media;
        this.streamUrlContainer = movie;
        this.tmdbMedia = tmdbMovie;
        this.type = MediaType.MOVIE;
    }

    public MediaContainer(Media media, TvSeries tvSeries, TmdbTvSeries tmdbTvSeries) {
        this.media = media;
        this.streamUrlContainer = tvSeries;
        this.tmdbMedia = tmdbTvSeries;
        this.type = MediaType.TV_SERIES;
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
