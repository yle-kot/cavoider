package com.example.cavoid.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationDao {
    @Query("SELECT * FROM pastLocation")
    List<PastLocation> getAll();

    @Query("SELECT * FROM pastLocation WHERE date IN (:dates)")
    List<PastLocation> loadAllByDates(int[] dates);

    @Query("SELECT * FROM pastLocation WHERE fips LIKE :code LIMIT 1")
    PastLocation findByLocation(String code);

    @Insert
    void insertAll(PastLocation... pastLocations);

    @Delete
    void delete(PastLocation pastLocation);
}
