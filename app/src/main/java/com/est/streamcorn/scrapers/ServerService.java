package com.est.streamcorn.scrapers;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import com.est.streamcorn.scrapers.servers.*;
import com.est.streamcorn.utils.RegexpUtils;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.util.regex.Pattern;

public class ServerService {

    @StringDef({ServerType.SWZZ, ServerType.VCRYPT, ServerType.OPENLOAD, ServerType.SPEEDVIDEO, ServerType.WSTREAM, ServerType.VERYSTREAM})
    public @interface ServerType {
        String SWZZ = "swzz.xyz";
        String VCRYPT = "vcrypt.net";
        String OPENLOAD = "openload.co";
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
    private static Server getServerInstance(String url, Context context) {
        String domain = getDomain(url);
        if (domain == null) return null;
        switch (domain) {
            case ServerType.SWZZ:
                return new Swzz();
            case ServerType.VCRYPT:
                return new Vcrypt();
            case ServerType.OPENLOAD:
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
        Server server = getServerInstance(url, context);
        if (server == null) {
            return Single.error(new UnsupportedOperationException("Server for " + url + " not supported"));
        } else if (server.isVideo()) {
            return server.resolve(url, context);
        } else {
            return server.resolve(url, context)
                    .observeOn(Schedulers.computation())
                    .flatMap(resolvedUrl -> resolveRecursive(url, context));
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
