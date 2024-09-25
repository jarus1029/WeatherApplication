package com.suraj.weatherapplication.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWeather(WeatherEntity weatherEntity);

    @Query("SELECT * FROM weather WHERE LOWER(cityName) = :cityName LIMIT 1")
    WeatherEntity getWeatherByCity(String cityName);

    @Query("SELECT * FROM weather ORDER BY timestamp DESC LIMIT 10")
    List<WeatherEntity> getAllWeatherData();


}
