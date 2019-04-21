package com.est.streamcorn.models;

import java.util.ArrayList;

/**
 * Created by Matteo on 30/01/2018.
 */

public class Episode {

    private ArrayList<StreamUrl> urls;

    public Episode(){
        urls = new ArrayList<>();
    }

    public void addUrl(StreamUrl streamUrl){
        urls.add(streamUrl);
    }

    public ArrayList<StreamUrl> getUrls(){
        return urls;
    }
}
