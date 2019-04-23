package com.est.streamcorn.persistence.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.est.streamcorn.persistence.models.Media;
import com.est.streamcorn.scrapers.models.MediaType;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.List;

@Dao
public interface MediaDao {
    @Query("SELECT COUNT(*) > 0 FROM Media WHERE url LIKE :url")
    Single<Boolean> contains(String url);

    @Query("SELECT * FROM Media WHERE type == " + MediaType.MOVIE)
    Single<List<Media>> getAllMovies();

    @Query("SELECT * FROM Media WHERE title LIKE '%' || :title || '%' AND type == " + MediaType.MOVIE)
    Single<List<Media>> getMoviesByTitle(String title);

    @Query("SELECT * FROM Media WHERE type == " + MediaType.TV_SERIES)
    Single<List<Media>> getAllTvSeries();

    @Query("SELECT * FROM Media WHERE title LIKE '%' || :title || '%' AND type == " + MediaType.TV_SERIES)
    Single<List<Media>> getTvSeriesByTitle(String title);

    @Insert
    Completable insert(Media media);

    @Query("DELETE FROM Media WHERE url LIKE :url")
    Completable delete(String url);
}
