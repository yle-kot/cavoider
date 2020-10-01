package com.example.cavoid;


import androidx.room.Database;
import androidx.room.RoomDatabase;

    @Database(entities = {PastLocation.class}, version = 1, exportSchema = false)
    public abstract class LocationDatabase extends RoomDatabase {
        public abstract LocationDao locationDao();
    }
