package com.suraj.weatherapplication.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "weather")
public class WeatherEntity {
    @PrimaryKey
    @NonNull
    public String cityName;

    @TypeConverters(WeatherDataConverter.class)
    public WeatherData weatherData;
    public long timestamp;

    public WeatherEntity() {
    }

    public WeatherEntity(String cityName, WeatherData weatherData, long timestamp) {
        this.cityName = cityName;
        this.weatherData = weatherData;
        this.timestamp = timestamp;
    }

    @NonNull
    public String getCityName() {
        return cityName;
    }
    public WeatherData getWeatherData() {
        return weatherData;
    }

}

