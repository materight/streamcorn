package com.materight.streamcorn.scrapers.utils;

public class InfoExtractor {

    public static String getTitle(String dirtyTitle) {
        return dirtyTitle.replaceAll("[(\\[].*?[)\\]] ?", "");
    }

}
