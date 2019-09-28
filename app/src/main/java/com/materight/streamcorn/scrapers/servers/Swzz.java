package com.materight.streamcorn.scrapers.servers;

import android.content.Context;
import android.util.Log;
import com.materight.streamcorn.scrapers.utils.NetworkUtils;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.jsoup.nodes.Element;

public class Swzz extends Server {

    private static final String TAG = "Swzz";

    @Override
    public Single<String> resolve(String url, Context context) {
        if (!url.contains("embed")) url += "embed/552x352/";
        return NetworkUtils.downloadPageHeadless(url, context)
                .observeOn(Schedulers.computation())
                .map(document -> {
                    Log.e(TAG, "PAGE DOWNLOADED");
                    Element element = document.selectFirst("a[href].btn-wrapper.link");
                    String parsedUrl = element.attr("href");
                    if (!parsedUrl.startsWith("http"))
                        parsedUrl = "https:" + parsedUrl;
                    return parsedUrl;
                });
    }

    @Override
    public boolean isVideo() {
        return false;
    }
}
