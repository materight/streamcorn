package com.materight.streamcorn.scrapers.servers;

import android.content.Context;
import com.materight.streamcorn.scrapers.utils.NetworkUtils;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.jsoup.nodes.Element;

public class Verystream extends Server {

    private static final String TAG = "Verystream";

    @Override
    public Single<String> resolve(String url, Context context) {
        return NetworkUtils.downloadPageHeadless(url, context)
                .observeOn(Schedulers.computation())
                .map(document -> {
                    Element element = document.selectFirst("#videolink");
                    return "https://verystream.com/gettoken/" + element.html() + "?mime=true";
                });
    }

    @Override
    public boolean isVideo() {
        return true;
    }
}