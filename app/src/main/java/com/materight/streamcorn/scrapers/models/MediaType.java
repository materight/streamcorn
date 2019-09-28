package com.materight.streamcorn.scrapers.models;

import androidx.annotation.IntDef;

@IntDef({MediaType.MOVIE, MediaType.TV_SERIES, MediaType.UNKNOWN})
public @interface MediaType {
    int MOVIE = 0;
    int TV_SERIES = 1;
    int UNKNOWN = 2;
}

