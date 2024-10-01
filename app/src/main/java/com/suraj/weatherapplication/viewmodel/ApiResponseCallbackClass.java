package com.suraj.weatherapplication.viewmodel;

import com.suraj.weatherapplication.model.WeatherData;

public class ApiResponseCallbackClass {

    public  interface ApiResponseCallback {
        void onSuccess(WeatherData weatherData);
        void onFailure(String errorMessage);
    }
}
