package com.example.cavoid.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.time.LocalDate;

@Entity(tableName = "past_location",
        primaryKeys = {"date", "fips"}
//        foreignKeys = {
//                @ForeignKey(
//                        entity = ActiveCases.class,
//                        parentColumns = "fips",
//                        childColumns = "fips")
//        }
)
public class PastLocation {

    @NonNull
    @ColumnInfo(name = "date")
    public LocalDate date;

    @NonNull
//    @ForeignKey(entity = ActiveCases.class, parentColumns = "fips", childColumns = "fips")
    @ColumnInfo(name = "fips")
    public String fips;

    @ColumnInfo(name = "was_notified")
    public Boolean wasNotified = false;
}
