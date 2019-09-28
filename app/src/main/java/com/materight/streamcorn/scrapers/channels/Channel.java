package com.materight.streamcorn.scrapers.channels;

import android.content.Context;
import com.materight.streamcorn.scrapers.models.Media;
import com.materight.streamcorn.scrapers.models.MediaType;
import com.materight.streamcorn.scrapers.models.Movie;
import com.materight.streamcorn.scrapers.models.TvSeries;
import com.materight.streamcorn.scrapers.utils.NetworkUtils;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

/**
 * Base class per tutti i canali
 */
public abstract class Channel {

    private Single<Document> downloadPage(String url, Context context) {
        if (this.getProperties().needHeadlessRequest()) {
            return NetworkUtils.downloadPageHeadless(url, context);
        } else {
            return NetworkUtils.downloadPage(url);
        }
    }

    public Single<ArrayList<Media>> getMovieList(int page, Context context) {
        return this.downloadPage(getProperties().getMovieListUrl(page), context)
                .observeOn(Schedulers.computation())
                .map(this::parseMovieList)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ArrayList<Media>> getTvSeriesList(int page, Context context) {
        return this.downloadPage(getProperties().getTvSeriesListUrl(page), context)
                .observeOn(Schedulers.computation())
                .map(this::parseTvSeriesList)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ArrayList<Media>> searchMovie(String query, int page, Context context) {
        return this.downloadPage(getProperties().getMovieSearchUrl(query, page), context)
                .observeOn(Schedulers.computation())
                .map(this::parseSearchedMovieList)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ArrayList<Media>> searchTvSeries(String query, int page, Context context) {
        return this.downloadPage(getProperties().getTvSeriesSearchUrl(query, page), context)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(this::parseSearchedTvSeriesList)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Movie> getMovie(final String url, Context context) {
        return this.downloadPage(url, context)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(this::parseMovieDetail)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<TvSeries> getTvSeries(final String url, Context context) {
        return this.downloadPage(url, context)
                .observeOn(Schedulers.computation())
                .map(this::parseTvSeriesDetail)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single getMedia(final String url, @MediaType int mediaType, Context context) {
        switch (mediaType) {
            case MediaType.MOVIE:
                return getMovie(url, context);
            case MediaType.TV_SERIES:
                return getTvSeries(url, context);
            case MediaType.UNKNOWN:
            default:
                //  Nel caso il tipo di media aperto nel channel sia sconosciuto, cerco di capire che tipo è
                return this.downloadPage(url, context)
                        .observeOn(Schedulers.computation())
                        .map(document -> {
                            Movie m = parseMovieDetail(document);
                            if (m.getUrls().size() == 0)  //Try to parse as movie. If fail, parse as Tv series
                                return parseTvSeriesDetail(document);
                            else
                                return m;
                        })
                        .observeOn(AndroidSchedulers.mainThread());
        }
    }

    protected abstract ArrayList<Media> parseMovieList(Document document) throws Exception;

    protected abstract ArrayList<Media> parseTvSeriesList(Document document) throws Exception;

    protected abstract ArrayList<Media> parseSearchedMovieList(Document document) throws Exception;

    protected abstract ArrayList<Media> parseSearchedTvSeriesList(Document document) throws Exception;

    protected abstract Movie parseMovieDetail(Document document) throws Exception;

    protected abstract TvSeries parseTvSeriesDetail(Document document) throws Exception;

    public abstract ChannelProperties getProperties();
}
