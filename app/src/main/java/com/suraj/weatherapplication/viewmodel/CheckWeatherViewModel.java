package com.suraj.weatherapplication.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.suraj.weatherapplication.model.WeatherData;
import com.suraj.weatherapplication.model.WeatherEntity;

import java.util.List;

public class CheckWeatherViewModel extends AndroidViewModel {
    private WeatherRepository weatherRepository;

    public CheckWeatherViewModel(Application application) {
        super(application);
        weatherRepository = new WeatherRepository(application.getApplicationContext());
    }

    public LiveData<List<WeatherEntity>> getAllWeatherData() {
        return weatherRepository.getAllWeatherData();
    }

    public LiveData<String> getErrorMessage() {
        return weatherRepository.getErrorMessage();
    }

    public LiveData<WeatherData> getWeatherData() {
        return weatherRepository.getWeatherData();
    }

    public LiveData<Boolean> getIsLoading() {
        return weatherRepository.getIsLoading();
    }

    public void checkWeather(String city) {
        weatherRepository.checkWeather(city, getApplication());
    }

    public LiveData<Boolean> getIsNewApiRequest() {
        return weatherRepository.getIsNewApiRequest();
    }

}


