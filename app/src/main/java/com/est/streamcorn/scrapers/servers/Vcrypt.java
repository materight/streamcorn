package com.est.streamcorn.scrapers.servers;

import android.content.Context;
import com.est.streamcorn.scrapers.utils.NetworkUtils;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class Vcrypt extends Server {

    private static final String TAG = "Vcrypt";

    @Override
    public Single<String> resolve(String url, Context context) {
        //  TODO: fix
        url = url.replaceFirst("shield", "opencryptz1")
                .replaceFirst("wss", "wss1");
        final String finalUrl = url;

        return NetworkUtils.downloadPage(url)
                .observeOn(Schedulers.computation())
                .map(document -> {
                    if (finalUrl.contains("opencryptz1")) {
                        return document.selectFirst("iframe[src]").attr("src");
                    } else {
                        return document.location().equals(finalUrl) ? null : document.location();
                    }
                });
    }

    @Override
    public boolean isVideo() {
        return false;
    }
}
