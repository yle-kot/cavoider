package com.operationcodify.cavoid.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The location database is the core of our database object. It creates a single, static instance of
 * of the database using the application's context. The Database is defined as containing the entities
 * in the @ symbol. The locationDao is linked in the body of the class. In order to get an instance
 * of the dao, you will need to call LocationDatabase.getLocationDao()
 * <p>
 * In order to perform write operations on the database:
 * LocationDao locDao = LocationDatabase.getLocationDao();
 * LocationDatabase.databaseWriteExecutor.execute( () -> locDao.writeMethodHere() );
 */
@Database(entities = {PastLocation.class, ActiveCases.class, NotifiedLocation.class}, version = 6, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class LocationDatabase extends RoomDatabase {
    public static final String DB_NAME = "locations_db";
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public static volatile LocationDatabase INSTANCE;

    public static LocationDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LocationDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                                    LocationDatabase.class, DB_NAME)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract LocationDao locationDao();

    public LocationDao getLocationDao() {
        return locationDao();
    }
}