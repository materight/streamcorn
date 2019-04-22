package com.est.streamcorn.scrapers.servers;

import android.content.Context;
import io.reactivex.Single;

/**
 * Base class per tutti i server
 */
public abstract class Server {
    /**
     * Metodo per risolvere un url di un server
     *
     * @param url     url da risolvere
     * @param context necessario per le richieste headless
     * @return Single per url risolto. Può essere di un video o può essere di un altro sito
     */
    public abstract Single<String> resolve(String url, Context context);


    /**
     * Proprietà del server
     *
     * @return true se l'url restituito è un video, false se necessita di altri processamenti
     */
    public abstract boolean isVideo();
}
