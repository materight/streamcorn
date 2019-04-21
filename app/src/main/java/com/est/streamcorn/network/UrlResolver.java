package com.est.streamcorn.network;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Matteo on 31/12/2017.
 */

public class UrlResolver{

    private static final String TAG = "UrlResolver";

    private static final Pattern GET_DOMAIN = Pattern.compile("^(?:https?:\\/\\/)?(?:[^@\\/\\n]+@)?(?:www\\.)?([^:\\/\\n]+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private Context context;

    public  UrlResolver(Context context){
        this.context = context;
    }

    private interface Domain{
        String SWZZ =  "swzz.xyz";
        String VCRYPT = "vcrypt.net";
        String OPENLOAD = "openload.co";
        String SPEEDVIDEO = "speedvideo.net";
        String WSTREAM = "wstream.video";
        String VERYSTREAM = "verystream.com";
    }

    @Nullable
    private static String getFirstMatch(Pattern p, String string){
        Matcher m = p.matcher(string);
        if (m.find())
            return m.group(1);
        else
            return null;
    }

    @Nullable
    public static String getTitle(String string){
        return string.replaceAll("[\\(\\[].*?[\\)\\]] ?", "");
    }

    @Nullable
    private static String getDomain(String url){
        return getFirstMatch(GET_DOMAIN, url);
    }

    public Maybe<String> resolveUrl(final String url){
        return Maybe.create((MaybeEmitter<String> emitter) -> {
            Single<String> resolver;
            final boolean recursive;
            Log.d(TAG, "Resolving " + url + " ...");
            String domain = getDomain(url);
            switch (domain) {
                case Domain.SWZZ:
                    recursive = true;
                    resolver = resolveSwzz(url);
                    break;
                case Domain.VCRYPT:
                    recursive = true;
                    resolver = resolveVcrypt(url);
                    break;

                case Domain.OPENLOAD:
                    recursive = false;
                    resolver = resolveOpenload(url);
                    break;
                case Domain.SPEEDVIDEO:
                    recursive = false;
                    resolver = resolveSpeedvideo(url);
                    break;
                case Domain.WSTREAM:
                    recursive = false;
                    resolver = resolveWstream(url);
                    break;
                case Domain.VERYSTREAM:
                    recursive = false;
                    resolver = resolveVeryStream(url);
                    break;
                default: recursive = false; resolver = null;
            }
            if(resolver != null) {
                resolver.observeOn(Schedulers.computation())
                        .subscribe(response -> {
                            if(recursive) {
                                resolveUrl(response).subscribe(emitter::onSuccess, emitter::onError, emitter::onComplete);
                            }
                            else {
                                emitter.onSuccess(response);
                            }
                        }, emitter::onError);
            }
            else {
                Log.d(TAG, "Server not supported");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation());
    }

    private Single<String> resolveSwzz(String url){
        if(!url.contains("embed"))
            url += "embed/552x352/";
        Log.d(TAG, "Loading SWZZ url from: "+ url);
        return NetworkUtils.downloadPageHeadless(url, context)
                .observeOn(Schedulers.computation())
                .map(document -> {
                    Element element = document.selectFirst("a[href].btn-wrapper.link");
                    String pageUrl = null;
                    if(element != null) {
                        pageUrl = element.attr("href");
                        if(!pageUrl.startsWith("http")){
                            pageUrl = "https:" +pageUrl;
                        }
                    }
                    return pageUrl;
                });
    }


    private static Single<String> resolveVcrypt(String url){
        if(url.contains("fastshield")){
            url = url.replace("fastshield", "opencryptz1");
        }
        final String parsedUrl = url;
        Log.d(TAG, "Loading VCRYPT url from: "+ parsedUrl);
        return NetworkUtils.downloadPage(parsedUrl)
                .observeOn(Schedulers.computation())
                .map(document -> {
                    if(parsedUrl.contains("opencryptz1")){
                        //Try to getUrl opencrypt link
                        Element element = document.selectFirst("iframe[src]");
                        if(element != null)
                            return element.attr("src");
                        return null;
                    }
                    else //Return automatic redirect
                        return document.location().equals(parsedUrl) ? null : document.location();
                });
    }

    private static final Pattern GET_OPENLOAD_URL = Pattern.compile("https?:\\/\\/(?:openload\\.(?:co|io)|oload\\.tv)\\/(?:f|embed)\\/([\\w\\-]+)");

    private Single<String> resolveOpenload(String url){
        url = "https://openload.co/embed/" + getFirstMatch(GET_OPENLOAD_URL, url) + "/";
        Log.d(TAG, "Loading OPENLOAD url from: "+ url);
        return NetworkUtils.downloadPageHeadless(url, 500, context)
                .observeOn(Schedulers.computation())
                .map(document -> {
                    Element element = document.selectFirst("div[style*=\"display:none\"] p:last-of-type");
                    String parsedUrl = null;
                    if(element != null)
                        parsedUrl = "https://openload.co/stream/" + element.html() + "?mime=true";
                    Log.d(TAG, "Parsed OPENLOAD url: " + parsedUrl);
                    return parsedUrl;
                });
    }

    private static final Pattern GET_SPEEDVIDEO_URL = Pattern.compile("https?:\\/\\/speedvideo\\.net\\/(.*)\\/");
    private static final Pattern GET_SPEEDVIDEO_FILE_URL = Pattern.compile("linkfile =\"([^\"]+)\"");

    private Single<String> resolveSpeedvideo(String url) {
        if(!url.contains("embed"))
            url = "https://speedvideo.net/embed-" + getFirstMatch(GET_SPEEDVIDEO_URL, url) + "-1x1.html";
        Log.d(TAG, "Loading SPEEDVIDEO url from: "+ url);
        return NetworkUtils.downloadPageHeadless(url, context)
                .observeOn(Schedulers.computation())
                .map(document -> {
                    String parsedUrl = getFirstMatch(GET_SPEEDVIDEO_FILE_URL, document.html());
                    Log.d(TAG, "Parsed SPEEDVIDEO url: " + parsedUrl);
                    return parsedUrl;
                });
    }

    private Single<String> resolveWstream(String url){
        Log.d(TAG, "Loading WSTREAM url from: "+ url);
        return NetworkUtils.downloadPageHeadless(url, context)
                .observeOn(Schedulers.computation())
                .map(document -> {
                    Element element = document.selectFirst("video[data-html5-video][src]");
                    String parsedUrl = null;
                    if(element != null)
                        parsedUrl = element.attr("src");
                    Log.d(TAG, "Parsed WSTREAM url: " + url);
                    return parsedUrl;
                });
    }

    private Single<String> resolveVeryStream(String url){
        Log.d(TAG, "Loading VERYSTREAM url from: "+ url);
        return NetworkUtils.downloadPageHeadless(url, context)
                .observeOn(Schedulers.computation())
                .map(document -> {
                    Element element = document.selectFirst("#videolink");
                    String parsedUrl = null;
                    if(element != null)
                        parsedUrl = "https://verystream.com/gettoken/"+ element.html() +"?mime=true";
                    Log.d(TAG, "Parsed VERYSTREAM url: " + parsedUrl);
                    return parsedUrl;
                });
    }
}
