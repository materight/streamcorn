package com.est.streamcorn.scrapers;

import android.content.Context;
import android.util.Log;
import androidx.annotation.Nullable;
import com.est.streamcorn.scrapers.servers.*;
import com.est.streamcorn.utils.RegexpUtils;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.util.regex.Pattern;

public class ServerService {

    public @interface ServerType {
        //  URL resolver
        String SWZZ = "swzz.xyz";
        String VCRYPT = "vcrypt.net";
        String RAPIDCRYPT = "rapidcrypt.net";
        String FILMSENZALIMITI = "filmsenzalimiti.beer";
        String ILGENIODELLOSTREAMING = "ilgeniodellostreaming.pw";

        //  Video streaming
        String OPENLOAD1 = "openload.co";
        String OPENLOAD2 = "openloads.co";
        String SPEEDVIDEO = "speedvideo.net";
        String WSTREAM = "wstream.video";
        String VERYSTREAM = "verystream.com";
    }

    private static final Pattern GET_DOMAIN = Pattern.compile("^(?:https?:\\/\\/)?(?:[^@\\/\\n]+@)?(?:www\\.)?([^:\\/\\n]+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    @Nullable
    private static String getDomain(String url) {
        return RegexpUtils.getFirstMatch(GET_DOMAIN, url);
    }

    @Nullable
    private static Server getServerInstance(String domain) {
        if (domain == null) return null;
        switch (domain) {
            //  URL resolver
            case ServerType.SWZZ:
                return new Swzz();
            case ServerType.VCRYPT:
                return new Vcrypt();
            case ServerType.RAPIDCRYPT:
                return new Rapidcrypt();
            case ServerType.FILMSENZALIMITI:
                return new FilmSenzaLimitiServer();
            case ServerType.ILGENIODELLOSTREAMING:
                return new IlGenioDelloStreamingServer();
            //  Video streaming
            case ServerType.OPENLOAD1:
            case ServerType.OPENLOAD2:
                return new Openload();
            case ServerType.SPEEDVIDEO:
                return new Speedvideo();
            case ServerType.WSTREAM:
                return new Wstream();
            case ServerType.VERYSTREAM:
                return new Verystream();
            default:
                return null;
        }
    }

    /**
     * Metodo ricorsivo, cerca di risolvere gli url fino a quando non ottiene l'url di un video
     */
    private static Single<String> resolveRecursive(String url, Context context) {
        final String domain = getDomain(url);
        url = RegexpUtils.httpToHttps(url);
        Log.d(domain, "Resolving: " + url);

        Server server = getServerInstance(domain);
        if (server == null) {
            return Single.error(new UnsupportedOperationException("Server for " + url + " not supported"));
        } else if (server.isVideo()) {
            return server.resolve(url, context)
                    .map(resolvedUrl -> {
                        Log.d(domain, "Resolved: " + resolvedUrl);
                        return resolvedUrl;
                    });
        } else {
            return server.resolve(url, context)
                    .observeOn(Schedulers.computation())
                    .flatMap(resolvedUrl -> {
                        Log.d(domain, "Resolved: " + resolvedUrl);
                        return resolveRecursive(resolvedUrl, context);
                    });
        }
    }

    /**
     * Risolve l'url del server e restituisce il link al video
     *
     * @param url     url da risolvere
     * @param context context dell'activity corrente. Necessario per poter usare HeadlessBrowser
     * @return Single con url come risultato
     */
    public static Single<String> resolve(String url, Context context) {
        return resolveRecursive(url, context)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
