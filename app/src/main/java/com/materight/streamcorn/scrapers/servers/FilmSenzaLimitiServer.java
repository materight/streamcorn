package com.materight.streamcorn.scrapers.servers;

import android.content.Context;
import com.materight.streamcorn.scrapers.ServerService;
import com.materight.streamcorn.scrapers.utils.NetworkUtils;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class FilmSenzaLimitiServer extends Server {
    private static final String TAG = "FilmSenzaLimitiServer";

    @Override
    public Single<String> resolve(String url, Context context) {
        //  Necessario il referrer per ottenere il redirect dal server
        return NetworkUtils.downloadPage(url, "https://" + ServerService.ServerType.FILMSENZALIMITI + "/title/")
                .observeOn(Schedulers.computation())
                .map(document -> document.location());
    }

    @Override
    public boolean isVideo() {
        return false;
    }
}
