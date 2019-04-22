package com.est.streamcorn.persistence;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.est.streamcorn.persistence.daos.MediaDao;
import com.est.streamcorn.persistence.models.Media;

@Database(entities = {Media.class}, version = 1)
public abstract class LibraryDatabase extends RoomDatabase {

    private static LibraryDatabase instance = null;

    public static LibraryDatabase getLibraryDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), LibraryDatabase.class,
                    "library-database").build();
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    public abstract MediaDao mediaDao();

}