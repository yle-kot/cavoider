package com.example.cavoid.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PastLocation {
    @NonNull
    @PrimaryKey
    public String date;

    @ColumnInfo(name = "fips")
    public String fips;

    @ColumnInfo(name = "wasNotified")
    public Boolean wasNotified;
}
