package com.est.streamcorn.scrapers.servers;

import android.content.Context;
import com.est.streamcorn.scrapers.utils.NetworkUtils;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class Rapidcrypt extends Server {

    private static final String TAG = "Rapidcrypt";

    @Override
    public Single<String> resolve(String url, Context context) {
        return NetworkUtils.downloadPage(url)
                .observeOn(Schedulers.computation())
                .map(document -> document.selectFirst("a.push_button.blue[href]").attr("href"));
    }

    @Override
    public boolean isVideo() {
        return false;
    }
}
