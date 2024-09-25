package com.suraj.weatherapplication.model;

import com.suraj.weatherapplication.data.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {

    @GET("weather")
    Call<WeatherResponse> getWeather(@Query("q") String city, @Query("appid") String apiKey);
}
