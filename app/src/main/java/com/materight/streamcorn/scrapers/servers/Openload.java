package com.materight.streamcorn.scrapers.servers;

import android.content.Context;
import com.materight.streamcorn.scrapers.utils.NetworkUtils;
import com.materight.streamcorn.utils.RegexpUtils;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.jsoup.nodes.Element;

import java.util.regex.Pattern;

public class Openload extends Server {

    private static final String TAG = "Openload";

    private static final Pattern GET_OPENLOAD_URL = Pattern.compile("https?://(?:openloads\\.(?:co|io)|oload\\.tv)/(?:f|embed|c2)/([\\w\\-]+)");

    @Override
    public Single<String> resolve(String url, Context context) {
        url = "https://openload.co/embed/" + RegexpUtils.getFirstMatch(GET_OPENLOAD_URL, url) + "/";

        return NetworkUtils.downloadPageHeadless(url, 500, context)
                .observeOn(Schedulers.computation())
                .map(document -> {
                    Element element = document.selectFirst("div[style*=\"display:none\"] p:last-of-type");
                    return "https://openload.co/stream/" + element.html() + "?mime=true";
                });
    }

    @Override
    public boolean isVideo() {
        return true;
    }
}
