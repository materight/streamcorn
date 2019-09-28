package com.materight.streamcorn.scrapers.servers;

import android.content.Context;
import com.materight.streamcorn.scrapers.utils.NetworkUtils;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class IlGenioDelloStreamingServer extends Server {
    private static final String TAG = "IlGenioDelloStreamingServer";

    @Override
    public Single<String> resolve(String url, Context context) {
        return NetworkUtils.downloadPageHeadless(url, context)
                .observeOn(Schedulers.computation())
                .map(document -> document.selectFirst("iframe.metaframe[src]").attr("src"));
    }

    @Override
    public boolean isVideo() {
        return false;
    }
}
