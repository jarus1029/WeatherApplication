package com.suraj.weatherapplication.viewmodel;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;

import com.suraj.weatherapplication.data.APIConstants;
import com.suraj.weatherapplication.model.AppDatabase;
import com.suraj.weatherapplication.model.WeatherApi;
import com.suraj.weatherapplication.model.WeatherDao;
import com.suraj.weatherapplication.model.WeatherData;
import com.suraj.weatherapplication.model.WeatherEntity;
import com.suraj.weatherapplication.data.WeatherResponse;
import com.suraj.weatherapplication.utils.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherRepository {
    private static WeatherDao weatherDao;
    private WeatherApi weatherApi;

    public WeatherRepository(Context context) {
        weatherDao = AppDatabase.getDatabase(context).weatherDao();
        weatherApi = createWeatherApi();
    }

    private WeatherApi createWeatherApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WeatherApi.class);
    }

    public List<WeatherEntity> getAllWeatherData() {
        return weatherDao.getTop10WeatherDataFromDb();
    }

    public WeatherEntity getWeatherByCity(String city) {
        return weatherDao.getWeatherByCity(city.toLowerCase());
    }

    public void insertWeather(WeatherEntity weatherEntity, WeatherResponse weatherResponse, ApiResponseCallbackClass.ApiResponseCallback responseCallback ) {
        new Thread(() -> {
            weatherDao.insertWeather(weatherEntity);
            WeatherData weatherDt=getWeatherDataFromWeatherResponse(weatherResponse);
            responseCallback.onSuccess(weatherDt);
        }).start();
    }

    public void fetchWeatherFromApi(String city, Context context, ApiResponseCallbackClass.ApiResponseCallback callback) {
        if (!NetworkUtils.isInternetAvailable(context)) {
            callback.onFailure("No internet connection! Please check your network.");
            return;
        }

        weatherApi.getWeather(city, APIConstants.API_KEY).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    WeatherData weatherDt=getWeatherDataFromWeatherResponse(weatherResponse);
                    WeatherEntity weatherEntity = new WeatherEntity(city.toLowerCase(), weatherDt, System.currentTimeMillis());
                    insertWeather(weatherEntity, weatherResponse, callback);
                } else {
                    handleErrorResponse(response, callback);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                callback.onFailure("Error fetching data: " + t.getMessage());
            }
        });
    }

    private void handleErrorResponse(Response<WeatherResponse> response, ApiResponseCallbackClass.ApiResponseCallback callback) {
        switch (response.code()) {
            case 404:
                callback.onFailure("City not found. Please check the city name !!");
                break;
            case 401:
                callback.onFailure("Unauthorized. Check your API key !!");
                break;
            case 500:
                callback.onFailure("Server error. Please try again later !!");
                break;
            default:
                callback.onFailure("Error fetching data: " + response.message());
                break;
        }
    }

    private WeatherData getWeatherDataFromWeatherResponse(WeatherResponse weatherResponse)
    {
        String weatherDescription = weatherResponse.getWeather().length > 0
                ? weatherResponse.getWeather()[0].getDescription()
                : "No data";
        double temperature = weatherResponse.getMain().getTemp() - 273.15;
        double feelsLike = weatherResponse.getMain().getFeelsLike() - 273.15;
        int humidity = weatherResponse.getMain().getHumidity();
        long pressure = weatherResponse.getMain().getPressure();
        String cityName = weatherResponse.getName();

        return new WeatherData(
                cityName,
                weatherDescription,
                temperature,
                feelsLike,
                humidity,
                pressure
        );
    }
}

