package com.operationcodify.cavoid.database;

import androidx.room.TypeConverter;

import org.joda.time.LocalDate;

public class Converters {
    /**
     * The converters are used to map a complex data type into something in the database and back.
     * For example, we can use LocalDate objects locally, and define a way of mapping them to and
     * a string in the database.

     */

    @TypeConverter
    public static LocalDate fromString(String value) {
        return value == null ? null : LocalDate.parse(value);
    }


    @TypeConverter
    public static String localDateToString(LocalDate date) {
        return date == null ? null : date.toString();
    }
}
