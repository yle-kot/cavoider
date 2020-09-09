package com.operationcodify.cavoid.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import java.util.List;

/**
 * The locationDao contains all of the access tools to perform IO on the database. See LocationDatabase
 * for information on how to get an instance of it.
 */

@Dao
public interface LocationDao {
    @Query( "SELECT pl.date, pl.fips, pl.county_name, pl.timestamp, ac.active_cases FROM past_location [pl]  " +
            "LEFT OUTER JOIN active_cases [ac] ON pl.fips = ac.fips")
    List<PastLocation> getAll();

    @Query( "SELECT pl.date, pl.fips, pl.county_name, pl.timestamp, ac.active_cases FROM past_location [pl]" +
            "LEFT OUTER JOIN active_cases as ac ON pl.fips = ac.fips" +
            " WHERE date IN (:dates)")
    List<PastLocation> loadAllByDates(LocalDate[] dates);

    @Query( "SELECT pl.date, pl.fips, pl.county_name, pl.timestamp, ac.active_cases FROM past_location [pl]" +
            "LEFT OUTER JOIN active_cases as ac ON pl.fips = ac.fips " +
            "ORDER BY pl.timestamp DESC " +
            "LIMIT 1")
    PastLocation getLatestLocation();

    @Query( "SELECT * FROM past_location [pl] " +
            "LEFT JOIN active_cases [ac] ON pl.fips == ac.fips " +
            "WHERE pl.fips LIKE :fips " +
            "ORDER BY pl.date DESC" +
            " LIMIT 1")
    PastLocation findByLocation(String fips);

    @Query("SELECT  DISTINCT pl.fips FROM past_location [pl]")
    List<String>getAllDistinctFips();

    @Query( "SELECT DISTINCT fips FROM notified_location ")
    List<String>getAllNotifiedFips();

    @Query( "SELECT pl.fips FROM past_location[pl] " +
            "LEFT JOIN notified_location[nl] ON pl.fips == nl.fips " +
            "WHERE nl.fips == null")
    List<String>getAllFipsToNotify();

    @Query( "Select nl.date FROM notified_location[nl] WHERE nl.fips == :fips")
    LocalDate getTimeOfLastNotificationFor(String fips);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLocations(PastLocation... pastLocations);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotifiedLocations(NotifiedLocation... NotifiedLocations);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReports(ActiveCases... activeCases);

    @Delete
    void delete(PastLocation pastLocation);

    @Update()
    void createNewNotificationEntry(NotifiedLocation... notifiedLocations);

    @Query( "DELETE FROM past_location WHERE date < :date; "
//            "DELETE FROM active_cases as ac " +
//            "OUTER JOIN past_locations as pl on ac.fips = pl.fips " +
//            "WHERE ac.fips != pl.fips"
    )
    void cleanRecordsOlderThan(LocalDate date);

    @Query( "SELECT EXISTS(SELECT 1 FROM notified_location[nl] WHERE nl.fips == :fips LIMIT 1)")
    int hasFipsBeenNotified(String fips);
}
