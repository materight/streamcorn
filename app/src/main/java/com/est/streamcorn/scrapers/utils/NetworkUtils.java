package com.est.streamcorn.scrapers.utils;

import android.content.Context;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class NetworkUtils {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

    public static Single<Document> downloadPage(final String url) {
        return Single.fromCallable(() -> Jsoup.connect(url).userAgent(USER_AGENT).get())
                .subscribeOn(Schedulers.io());
    }

    public static Single<Document> downloadPage(final String url, final String referrer) {
        return Single.fromCallable(() -> Jsoup.connect(url).userAgent(USER_AGENT).referrer(referrer).get())
                .subscribeOn(Schedulers.io());
    }

    public static Single<Document> downloadPageHeadless(final String url, final int delay, final Context context) {
        return Single.create((SingleEmitter<Document> emitter) -> {
            try {
                emitter.setDisposable(new HeadlessRequest(url, USER_AGENT, delay, context, emitter::onSuccess, emitter::onError));
            } catch (Exception e) {
                emitter.onError(e);
            }
        }).unsubscribeOn(AndroidSchedulers.mainThread()) //  La WebView deve essere eseguita nel mainThread essendo un componente grafico
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    public static Single<Document> downloadPageHeadless(final String url, final Context context) {
        return downloadPageHeadless(url, 0, context);
    }
}
