package com.example.cavoid.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {PastLocation.class, ActiveCases.class}, version = 3, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class LocationDatabase extends RoomDatabase {
    public abstract LocationDao locationDao();

    public static final String DB_NAME = "locations_db";


    public static volatile LocationDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static LocationDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (LocationDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            LocationDatabase.class, DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    public LocationDao getLocationDao() {
        return locationDao();
    }
}



