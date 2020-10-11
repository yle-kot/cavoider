package com.example.cavoid.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import org.joda.time.LocalDate;

@Entity(tableName = "active_cases",
        primaryKeys = {"fips"}
)
public class ActiveCases {

    @NonNull
    @ColumnInfo(name = "fips")
    public String fips;

    @ColumnInfo(name="active_cases")
    public int activeCases;

    @ColumnInfo(name="report_date")
    public LocalDate reportDate;

}
