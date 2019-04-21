package com.est.streamcorn.network;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Matteo on 28/02/2018.
 */

public class NetworkUtils {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

    public static Single<Document> downloadPage(final String url){
        return Single.fromCallable(() -> {
            return Jsoup.connect(url).userAgent(USER_AGENT).get();
        }).subscribeOn(Schedulers.io());
    }

    public static Single<Document> downloadPageHeadless(final String url, int waitMillis, final Context context){
        return Single.create((SingleEmitter<Document> emitter) -> {
            HeadlessRequest.create(url, context, waitMillis, emitter::onSuccess, emitter::onError);
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public static Single<Document> downloadPageHeadless(final String url, final Context context){
        return Single.create((SingleEmitter<Document> emitter) -> {
            HeadlessRequest.create(url, context, 0, emitter::onSuccess, emitter::onError);
        }).subscribeOn(AndroidSchedulers.mainThread());
    }
}
