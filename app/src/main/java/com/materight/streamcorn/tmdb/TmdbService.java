package com.materight.streamcorn.tmdb;

import com.materight.streamcorn.tmdb.models.*;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Matteo on 18/01/2018.
 */

public interface TmdbService {
    String API_KEY = "f16536bc1ab9192470ba5d372c2a99ff";
    String BASE_QUERY_PARAMETERS = "?language=it-IT&api_key=" + API_KEY;

    @GET("search/movie" + BASE_QUERY_PARAMETERS)
    Single<TmdbMovieList> searchMovie(@Query("query") String title);

    @GET("movie/{id}" + BASE_QUERY_PARAMETERS + "&append_to_response=videos")
    Single<TmdbMovie> getMovieDetails(@Path("id") int id);

    @GET("search/tv" + BASE_QUERY_PARAMETERS)
    Single<TmdbTvSeriesList> searchTvSeries(@Query("query") String title);

    @GET("tv/{id}" + BASE_QUERY_PARAMETERS + "&append_to_response=videos")
    Single<TmdbTvSeries> getTvSeriesDetails(@Path("id") int id);

    @GET("tv/{id}/season/{number}" + BASE_QUERY_PARAMETERS + "&append_to_response=videos")
    Single<TmdbSeason> getSeasonDetails(@Path("id") int id, @Path("number") int seasonNumber);
}