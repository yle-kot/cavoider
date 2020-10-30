package com.operationcodify.cavoid.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import org.joda.time.LocalDate;

@Entity(tableName = "notified_location",
        primaryKeys = {"fips"}

)
public class NotifiedLocation {
    @NonNull
    @ColumnInfo(name = "date")
    public LocalDate date;

    @NonNull
    @ColumnInfo(name = "fips")
    public String fips;
}
