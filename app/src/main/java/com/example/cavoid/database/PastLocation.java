package com.example.cavoid.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PastLocation {
    @PrimaryKey
    public String date;

    @ColumnInfo(name = "fips")
    public String fips;

    @ColumnInfo(name = "wasNotified")
    public Boolean wasNotified;
}
