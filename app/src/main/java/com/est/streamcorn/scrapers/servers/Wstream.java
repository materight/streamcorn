package com.est.streamcorn.scrapers.servers;

import android.content.Context;
import com.est.streamcorn.scrapers.utils.NetworkUtils;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class Wstream extends Server {

    private static final String TAG = "Wstream";

    @Override
    public Single<String> resolve(String url, Context context) {
        return NetworkUtils.downloadPageHeadless(url, context)
                .observeOn(Schedulers.computation())
                .map(document -> document.selectFirst("video[data-html5-video][src]").attr("src"));
    }

    @Override
    public boolean isVideo() {
        return true;
    }
}
