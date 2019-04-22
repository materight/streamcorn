package com.est.streamcorn.persistence.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.est.streamcorn.persistence.models.Media;
import com.est.streamcorn.scrapers.models.MediaType;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

import java.util.List;

@Dao
public interface MediaDao {
    @Query("SELECT COUNT(*) > 0 FROM Media WHERE url LIKE :url")
    Single<Boolean> contains(String url);

    @Query("SELECT * FROM Media WHERE type == " + MediaType.MOVIE)
    Flowable<Media> getAllMovies();

    @Query("SELECT * FROM Media WHERE type == " + MediaType.TV_SERIES)
    Flowable<Media> getAllTvSeries();

    @Insert
    Completable insert(Media media);
}
