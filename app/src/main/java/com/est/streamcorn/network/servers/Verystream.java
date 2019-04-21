package com.est.streamcorn.network.servers;

import android.content.Context;
import android.util.Log;
import com.est.streamcorn.network.utils.NetworkUtils;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.jsoup.nodes.Element;

public class Verystream extends Server {

    private static final String TAG = "Verystream";

    @Override
    public Single<String> resolve(String url, Context context) {
        Log.d(TAG, "Resolving: " + url);
        return NetworkUtils.downloadPageHeadless(url, context)
                .observeOn(Schedulers.computation())
                .map(document -> {
                    Element element = document.selectFirst("#videolink");
                    String parsedUrl = "https://verystream.com/gettoken/" + element.html() + "?mime=true";

                    Log.d(TAG, "Resolved: " + parsedUrl);
                    return parsedUrl;
                });
    }

    @Override
    public boolean isVideo() {
        return true;
    }
}