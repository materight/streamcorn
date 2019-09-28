package com.materight.streamcorn.scrapers.channels;

import android.content.Context;
import com.materight.streamcorn.R;
import com.materight.streamcorn.scrapers.ChannelService;
import com.materight.streamcorn.scrapers.models.Media;
import com.materight.streamcorn.scrapers.models.Movie;
import com.materight.streamcorn.scrapers.models.TvSeries;
import com.materight.streamcorn.scrapers.utils.NetworkUtils;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class VVVVID extends Channel {

    private static final String TAG = "VVVVID";

    private static final ChannelProperties properties = new ChannelProperties(ChannelService.ChannelType.CINEBLOG01,
            "VVVV<font color=\"%s\">ID</font>",
            R.drawable.channel_vvvvid,
            false,
            true,
            true,
            true,
            //  Non utlizzabili, serve una gestione diversa delle richieste
            page -> "vvvvid/ondemand/film/channel/10007/last",
            page -> "vvvvid/ondemand/film/channel/10007/last",
            query -> page -> String.format("vvvvid/ondemand/film/channel/10007/last?filter=%s&conn_id=", query),
            query -> page -> String.format("vvvvid/ondemand/film/channel/10007/last?filter=%sconn_id=", query));

    @Override
    public ChannelProperties getProperties() {
        return properties;
    }

    public Single<ArrayList<Media>> getMovieList(int page, Context context) {
        if (page > 0) return Single.just(new ArrayList<>());    //  Solo 1 pagina
        return NetworkUtils.downloadJSON(getProperties().getMovieListUrl(page))
                .observeOn(Schedulers.computation())
                .map(this::parseMovieList)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ArrayList<Media>> getTvSeriesList(int page, Context context) {
        return NetworkUtils.downloadJSON(getProperties().getTvSeriesListUrl(page))
                .observeOn(Schedulers.computation())
                .map(this::parseTvSeriesList)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ArrayList<Media>> searchMovie(String query, int page, Context context) {
        return NetworkUtils.downloadJSON(getProperties().getMovieSearchUrl(query, page))
                .observeOn(Schedulers.computation())
                .map(this::parseSearchedMovieList)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ArrayList<Media>> searchTvSeries(String query, int page, Context context) {
        return NetworkUtils.downloadJSON(getProperties().getTvSeriesSearchUrl(query, page))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(this::parseSearchedTvSeriesList)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Movie> getMovie(final String url, Context context) {
        return NetworkUtils.downloadJSON(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(this::parseMovieDetail)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<TvSeries> getTvSeries(final String url, Context context) {
        return NetworkUtils.downloadJSON(url)
                .observeOn(Schedulers.computation())
                .map(this::parseTvSeriesDetail)
                .observeOn(AndroidSchedulers.mainThread());
    }

    private ArrayList<Media> parseMovieList(String json) throws Exception {
        return null;
    }

    private ArrayList<Media> parseTvSeriesList(String json) throws Exception {
        return null;
    }

    private ArrayList<Media> parseSearchedMovieList(String json) throws Exception {
        return null;
    }

    private ArrayList<Media> parseSearchedTvSeriesList(String json) throws Exception {
        return null;
    }

    private Movie parseMovieDetail(String json) throws Exception {
        return null;
    }

    private TvSeries parseTvSeriesDetail(String json) throws Exception {
        return null;
    }


    //  Override per class astratta
    @Override
    protected ArrayList<Media> parseMovieList(Document document) throws Exception {
        return null;
    }

    @Override
    protected ArrayList<Media> parseTvSeriesList(Document document) throws Exception {
        return null;
    }

    @Override
    protected ArrayList<Media> parseSearchedMovieList(Document document) throws Exception {
        return null;
    }

    @Override
    protected ArrayList<Media> parseSearchedTvSeriesList(Document document) throws Exception {
        return null;
    }

    @Override
    protected Movie parseMovieDetail(Document document) throws Exception {
        return null;
    }

    @Override
    protected TvSeries parseTvSeriesDetail(Document document) throws Exception {
        return null;
    }


}
