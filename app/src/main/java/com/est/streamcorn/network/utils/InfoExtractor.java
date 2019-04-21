package com.est.streamcorn.network.utils;

public class InfoExtractor {

    public static String getTitle(String dirtyTitle) {
        return dirtyTitle.replaceAll("[(\\[].*?[)\\]] ?", "");
    }

}
