package com.materight.streamcorn.scrapers.models;

import java.io.Serializable;

/**
 * Created by Matteo on 30/12/2017.
 */

public class StreamUrl implements Serializable {

    private String name;
    private String url;
    private boolean isFile;

    public StreamUrl(String name, String url, boolean isFile) {
        this.name = name;
        this.url = url;
        this.isFile = isFile;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public boolean isFile() {
        return isFile;
    }
}
