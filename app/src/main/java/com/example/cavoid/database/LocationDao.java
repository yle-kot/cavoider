package com.example.cavoid.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.joda.time.LocalDate;
import java.util.List;

/**
 * The locationDao contains all of the access tools to perform IO on the database. See LocationDatabase
 * for information on how to get an instance of it.
 */

@Dao
public interface LocationDao {
    @Query( "SELECT * FROM past_location [pl]  " +
            "LEFT JOIN active_cases [ac] ON pl.fips == ac.fips")
    List<PastLocation> getAll();

    @Query( "SELECT * FROM past_location [pl]" +
            "LEFT JOIN active_cases as ac ON pl.fips == ac.fips" +
            " WHERE date IN (:dates)")
    List<PastLocation> loadAllByDates(LocalDate[] dates);

    @Query( "SELECT * FROM past_location [pl] " +
            "LEFT JOIN active_cases [ac] ON pl.fips == ac.fips " +
            "WHERE pl.fips LIKE :fips " +
            "ORDER BY pl.date DESC" +
            " LIMIT 1")
    PastLocation findByLocation(String fips);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLocations(PastLocation... pastLocations);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReports(ActiveCases... activeCases);

    @Delete
    void delete(PastLocation pastLocation);

    @Query( "DELETE FROM past_location WHERE date < :date; "
//            "DELETE FROM active_cases as ac " +
//            "OUTER JOIN past_locations as pl on ac.fips = pl.fips " +
//            "WHERE ac.fips != pl.fips"
    )
    void cleanRecordsOlderThan(LocalDate date);
}
