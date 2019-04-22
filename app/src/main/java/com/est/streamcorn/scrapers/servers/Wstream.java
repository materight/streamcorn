package com.est.streamcorn.scrapers.servers;

import android.content.Context;
import android.util.Log;
import com.est.streamcorn.scrapers.utils.NetworkUtils;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.jsoup.nodes.Element;

public class Wstream extends Server {

    private static final String TAG = "Wstream";

    @Override
    public Single<String> resolve(String url, Context context) {
        Log.d(TAG, "Resolving: " + url);
        return NetworkUtils.downloadPageHeadless(url, context)
                .observeOn(Schedulers.computation())
                .map(document -> {
                    Element element = document.selectFirst("video[data-html5-video][src]");
                    String parsedUrl = element.attr("src");

                    Log.d(TAG, "Resolved: " + url);
                    return parsedUrl;
                });
    }

    @Override
    public boolean isVideo() {
        return true;
    }
}
