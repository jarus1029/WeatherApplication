package com.suraj.weatherapplication.viewmodel;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.suraj.weatherapplication.data.APIConstants;
import com.suraj.weatherapplication.model.AppDatabase;
import com.suraj.weatherapplication.model.WeatherApi;
import com.suraj.weatherapplication.model.WeatherDao;
import com.suraj.weatherapplication.model.WeatherData;
import com.suraj.weatherapplication.model.WeatherEntity;
import com.suraj.weatherapplication.data.WeatherResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherRepository {
    private static WeatherDao weatherDao;
    private WeatherApi weatherApi;
    private static MutableLiveData<List<WeatherEntity>> allWeatherData;
    private static MutableLiveData<String> errorMessage;
    private MutableLiveData<WeatherData> weatherData;
    public static MutableLiveData<Boolean> isNewApiRequest;
    private static MutableLiveData<Boolean> isLoading;

    public WeatherRepository(Context context) {
        weatherDao = AppDatabase.getDatabase(context).weatherDao();
        weatherApi = createWeatherApi();
        allWeatherData = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        weatherData = new MutableLiveData<>();
        isNewApiRequest = new MutableLiveData<>(false);
        isLoading = new MutableLiveData<>(false);
    }

    private WeatherApi createWeatherApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WeatherApi.class);
    }

    public static MutableLiveData<List<WeatherEntity>> getAllWeatherData() {
        loadAllWeatherData();
        return allWeatherData;
    }

    public MutableLiveData<Boolean> getIsNewApiRequest() {
        return isNewApiRequest;
    }


    public static MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<WeatherData> getWeatherData() {
        return weatherData;
    }

    public static MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void checkWeather(final String city, Context context) {
        String formattedCity = city.toLowerCase();
        new Thread(() -> {
            WeatherEntity weatherEntity = weatherDao.getWeatherByCity(formattedCity);
            long currentTime = System.currentTimeMillis();

            if (weatherEntity != null) {
                if ((currentTime - weatherEntity.timestamp) < (4 * 60 * 60 * 1000)) {
                    weatherData.postValue(weatherEntity.weatherData);
                    isNewApiRequest.postValue(true);

                } else {
                    fetchWeatherFromApi(formattedCity, context);
                }
            } else {
                fetchWeatherFromApi(formattedCity, context);
            }
        }).start();
    }

    private void fetchWeatherFromApi(String city, Context context) {
        if (!isInternetAvailable(context)) {
            errorMessage.postValue("No internet connection! Please check your network.");
            return;
        }

        isLoading.postValue(true);

        weatherApi.getWeather(city, APIConstants.API_KEY).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    String weatherDescription = weatherResponse.getWeather().length > 0
                            ? weatherResponse.getWeather()[0].getDescription()
                            : "No data";
                    double temperature = weatherResponse.getMain().getTemp() - 273.15;
                    double feelsLike = weatherResponse.getMain().getFeelsLike() - 273.15;
                    int humidity = weatherResponse.getMain().getHumidity();
                    long pressure = weatherResponse.getMain().getPressure();
                    String cityName = weatherResponse.getName();

                    WeatherData weatherDt = new WeatherData(
                            cityName,
                            weatherDescription,
                            temperature,
                            feelsLike,
                            humidity,
                            pressure
                    );

                    WeatherEntity weatherEntity = new WeatherEntity(city, weatherDt, System.currentTimeMillis());
                    new Thread(() -> {
                        weatherDao.insertWeather(weatherEntity);
                        loadAllWeatherData();
                        weatherData.postValue(weatherDt);
                        isNewApiRequest.postValue(true);
                    }).start();
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                errorMessage.postValue("Error fetching data: " + t.getMessage());
            }
        });
    }

    private boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        } else {
            return connectivityManager.getActiveNetworkInfo() != null &&
                    connectivityManager.getActiveNetworkInfo().isConnected();
        }
    }

    private static void loadAllWeatherData() {
        new Thread(() -> {
            List<WeatherEntity> weatherDataList = weatherDao.getAllWeatherData();
            allWeatherData.postValue(weatherDataList);
        }).start();
    }

    private void handleErrorResponse(Response<WeatherResponse> response) {
        switch (response.code()) {
            case 404:
                errorMessage.postValue("City not found. Please check the city name !!");
                break;
            case 401:
                errorMessage.postValue("Unauthorized. Check your API key !!");
                break;
            case 500:
                errorMessage.postValue("Server error. Please try again later !!");
                break;
            default:
                errorMessage.postValue("Error fetching data: " + response.message());
                break;
        }
    }


}
