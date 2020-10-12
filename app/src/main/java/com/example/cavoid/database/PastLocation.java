package com.example.cavoid.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import org.joda.time.LocalDate ;

@Entity(tableName = "past_location",
        primaryKeys = {"date", "fips"}

)
public class PastLocation {

    @NonNull
    @ColumnInfo(name = "date")
    public LocalDate date;

    @NonNull
    @ColumnInfo(name = "fips")
    public String fips;

    @ColumnInfo(name = "was_notified")
    public Boolean wasNotified = false;
}
