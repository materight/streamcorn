package com.est.streamcorn.network.channels;

import android.util.Log;

import com.est.streamcorn.models.Media;
import com.est.streamcorn.models.Movie;
import com.est.streamcorn.models.TvSeries;
import com.est.streamcorn.network.NetworkUtils;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Matteo on 14/02/2018.
 */

public abstract class ChannelService {

    protected static final String TAG = "ChannelService";

    public static final int INVALID = -1;
    public static final int CINEBLOG01 = 0;
    public static final int FILMSENZALIMITI = 1;

    private static ChannelService instance;

    public static ChannelService getInstance(@Channel final int channelType){
        if(instance == null || instance.getChannelId() != channelType) {
            Log.d(TAG, "New channel service instance");
            switch (channelType) {
                case CINEBLOG01: instance = new Cineblog01(); break;
                case FILMSENZALIMITI: instance = new FilmSenzaLimiti(); break;
                default: instance = null; break;
            }
        }
        return instance;
    }

    public Single<ArrayList<Media>> getMovieList(int page){
        return NetworkUtils.downloadPage(getMovieListUrl(page))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(this::parseMovieList)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ArrayList<Media>> getTvSeriesList(int page){
        return NetworkUtils.downloadPage(getTvSeriesListUrl(page))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(this::parseTvSeriesList)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ArrayList<Media>> searchMovie(String query, int page){
        return NetworkUtils.downloadPage(getMovieSearchUrl(query, page))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(this::parseSearchMovieList)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ArrayList<Media>> searchTvSeries(String query, int page){
        return NetworkUtils.downloadPage(getTvSeriesSearchUrl(query, page))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(this::parseSearchTvSeriesList)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Movie> getMovie(String url){
        return NetworkUtils.downloadPage(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(this::parseMovie)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<TvSeries> getTvSeries(String url){
        return NetworkUtils.downloadPage(url)
                .observeOn(Schedulers.computation())
                .map(this::parseTvSeries)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single getMedia(String url, @Media.MediaType int mediaType){ //Unknown media type
        switch (mediaType){
            case Media.MOVIE: return getMovie(url);
            case Media.TV_SERIES: return getTvSeries(url);
            case Media.UNKNOWN: default:
                return NetworkUtils.downloadPage(url)
                        .observeOn(Schedulers.computation())
                        .map(document -> {
                            Movie m = parseMovie(document);
                            if(m.getUrls().size() == 0)  //Try to parse as movie. If fail, parse as Tv series
                                return parseTvSeries(document);
                            else
                                return m;
                        })
                        .observeOn(AndroidSchedulers.mainThread());
        }
    }

    abstract ArrayList<Media> parseMovieList(Document document) throws IOException;
    abstract ArrayList<Media> parseTvSeriesList(Document document) throws IOException;

    abstract ArrayList<Media> parseSearchMovieList(Document document) throws IOException;
    abstract ArrayList<Media> parseSearchTvSeriesList(Document document) throws IOException;

    abstract Movie parseMovie(Document document) throws IOException;
    abstract TvSeries parseTvSeries(Document document) throws IOException;

    abstract String getMovieListUrl(int page);
    abstract String getTvSeriesListUrl(int page);

    abstract String getMovieSearchUrl(String query, int page);
    abstract String getTvSeriesSearchUrl(String query, int page);

    public abstract String getHost();
    public abstract String getParametricName();
    public abstract int getBannerDrawable();
    public abstract boolean hasMovie();
    public abstract boolean hasTvSeries();
    public abstract boolean canSearch();
    @Channel
    public abstract int getChannelId();
}
