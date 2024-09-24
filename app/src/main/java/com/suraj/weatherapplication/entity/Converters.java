package com.suraj.weatherapplication.entity;

import androidx.room.TypeConverter;

public class Converters {

    @TypeConverter
    public static String fromStringArray(String[] data) {
        return data != null ? String.join(",", data) : null;
    }

    @TypeConverter
    public static String[] toStringArray(String data) {
        return data != null ? data.split(",") : null;
    }
}
