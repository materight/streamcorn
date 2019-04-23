package com.est.streamcorn.scrapers.models;

public abstract class MediaInterface {
    public abstract String getUrl();

    public abstract String getTitle();

    public abstract String getImageUrl();

    @MediaType
    public abstract int getType();

    public String getEscapedTitle() {
        return getTitle().replaceAll("’", "'");
    }
}
