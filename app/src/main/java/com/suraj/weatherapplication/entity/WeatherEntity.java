package com.suraj.weatherapplication.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weather")
public class WeatherEntity {
    @PrimaryKey
    @NonNull
    public String cityName;
    public String[] weatherData;
    public long timestamp;

    public WeatherEntity(String cityName, String[] weatherData, long timestamp) {
        this.cityName = cityName;
        this.weatherData = weatherData;
        this.timestamp = timestamp;
    }

    @NonNull
    public String getCityName() {
        return cityName;
    }

    public void setCityName(@NonNull String cityName) {
        this.cityName = cityName;
    }

    public String[] getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(String[] weatherData) {
        this.weatherData = weatherData;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

