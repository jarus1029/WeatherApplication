package com.suraj.weatherapplication.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.suraj.weatherapplication.model.WeatherData;
import com.suraj.weatherapplication.model.WeatherEntity;

import java.util.List;

public class CheckWeatherViewModel extends AndroidViewModel {
    private WeatherRepository weatherRepository;

    private MutableLiveData<List<WeatherEntity>> allWeatherData;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<WeatherData> weatherData;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<Boolean> isNewApiRequest;

    public CheckWeatherViewModel(Application application) {
        super(application);
        weatherRepository = new WeatherRepository(application.getApplicationContext());
        allWeatherData = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        weatherData = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        isNewApiRequest = new MutableLiveData<>(false);
    }

    public LiveData<List<WeatherEntity>> getAllWeatherData() {
        loadAllWeatherData();
        return allWeatherData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<WeatherData> getWeatherData() {
        return weatherData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsNewApiRequest() {
        return isNewApiRequest;
    }

    public void checkWeather(String city) {
        isLoading.setValue(true);

        new Thread(() -> {
            WeatherEntity weatherEntity = weatherRepository.getWeatherByCity(city);
            long currentTime = System.currentTimeMillis();

            if (weatherEntity != null && (currentTime - weatherEntity.timestamp) < (4 * 60 * 60 * 1000)) {
                weatherData.postValue(weatherEntity.weatherData);
                isNewApiRequest.postValue(false);
                isLoading.postValue(false);
            } else {
                weatherRepository.fetchWeatherFromApi(city, getApplication(), new WeatherRepository.ApiResponseCallback()
                {
                    @Override
                    public void onSuccess(WeatherData weatherDataResponse) {
                        new Thread(()->{
                            weatherData.postValue(weatherDataResponse);
                            isNewApiRequest.postValue(true);
                            isLoading.postValue(false);
                            allWeatherData.postValue(weatherRepository.getAllWeatherData());
                            loadAllWeatherData();
                        }).start();
                    }

                    @Override
                    public void onFailure(String errorMessageResponse) {
                        errorMessage.postValue(errorMessageResponse);
                        isLoading.postValue(false);
                    }
                });
            }
        }).start();
    }

    private void loadAllWeatherData() {
        new Thread(() -> {
            List<WeatherEntity> weatherDataList = weatherRepository.getAllWeatherData();
            Log.d("WeatherData", "Loaded data: " + weatherDataList.get(0).getCityName());
            allWeatherData.postValue(weatherDataList);
        }).start();
    }
}

