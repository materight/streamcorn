package com.est.streamcorn.tmdb;

import android.content.Context;

import com.est.streamcorn.tmdb.models.TmdbMovie;
import com.est.streamcorn.tmdb.models.TmdbSeason;
import com.est.streamcorn.tmdb.models.TmdbTvSeries;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.util.Date;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Matteo on 18/01/2018.
 */

public class TmdbClient {

    private static final String TAG = "TmdbClient";

    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    private static final int CACHE_SIZE = 10 * 1024 * 1024;

    private TmdbService tmdbService;
    private OkHttpClient okHttpClient;

    private static final OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

    private static final Gson gson =
            new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context1) -> {
                if(json == null || json.getAsString().isEmpty())
                    return null;
                return (new Gson()).getAdapter(Date.class).fromJsonTree(json);
            }).create();

    private static final Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create());


    public TmdbClient(Context context) {
        okHttpClient = httpClientBuilder
                .cache(new Cache(context.getCacheDir(), CACHE_SIZE))
                .build();

        Retrofit adapter = retrofitBuilder
                .client(okHttpClient)
                .build();
        tmdbService = adapter.create(TmdbService.class);
    }

    public Single<TmdbTvSeries> getTvSeriesDetail(String title){
        return tmdbService.searchTvSeries(title)
                .flatMap(tmdbTvSeriesList -> tmdbService.getTvSeriesDetails(tmdbTvSeriesList.getFirst().getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<TmdbMovie> getMovieDetail(String title){
        return tmdbService.searchMovie(title)
                .flatMap(tmdbMovieList -> tmdbService.getMovieDetails(tmdbMovieList.getFirst().getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<TmdbSeason> getSeasonDetails(int id, int seasonNumber){
        return tmdbService.getSeasonDetails(id, seasonNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
