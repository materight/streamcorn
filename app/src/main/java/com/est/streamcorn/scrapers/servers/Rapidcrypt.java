package com.est.streamcorn.scrapers.servers;

import android.content.Context;
import android.util.Log;
import com.est.streamcorn.scrapers.utils.NetworkUtils;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class Rapidcrypt extends Server {

    private static final String TAG = "Rapidcrypt";

    @Override
    public Single<String> resolve(String url, Context context) {
        Log.d(TAG, "Resolving: " + url);
        return NetworkUtils.downloadPage(url)
                .observeOn(Schedulers.computation())
                .map(document -> {
                    String parsedUrl = document.selectFirst("a.push_button.blue[href]").attr("href");
                    Log.d(TAG, "Resolved: " + parsedUrl);
                    return parsedUrl;
                });
    }

    @Override
    public boolean isVideo() {
        return false;
    }
}
