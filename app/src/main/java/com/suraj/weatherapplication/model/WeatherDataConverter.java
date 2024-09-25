package com.suraj.weatherapplication.model;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class WeatherDataConverter {

    @TypeConverter
    public static String fromWeatherData(WeatherData weatherData) {
        if (weatherData == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(weatherData);
    }

    @TypeConverter
    public static WeatherData toWeatherData(String weatherDataString) {
        if (weatherDataString == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(weatherDataString, WeatherData.class);
    }
}
