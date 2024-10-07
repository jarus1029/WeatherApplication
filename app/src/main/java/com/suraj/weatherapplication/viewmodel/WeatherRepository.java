package com.suraj.weatherapplication.viewmodel;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suraj.weatherapplication.data.APIConstants;
import com.suraj.weatherapplication.model.WeatherApi;
import com.suraj.weatherapplication.model.WeatherData;
import com.suraj.weatherapplication.model.WeatherEntity;
import com.suraj.weatherapplication.data.WeatherResponse;
import com.suraj.weatherapplication.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherRepository {

    private WeatherApi weatherApi;
    private DatabaseReference databaseReference;
    private String userEmail;

    public WeatherRepository(Context context) {
        weatherApi = createWeatherApi();
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_");
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userEmail);
    }

    private WeatherApi createWeatherApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WeatherApi.class);
    }

    public List<WeatherEntity> getAllWeatherData() {
        List<WeatherEntity> weatherEntities = new ArrayList<>();

        databaseReference.child("weatherData")
                .orderByChild("timestamp")
                .limitToLast(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<WeatherEntity> tempList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            WeatherEntity entity = snapshot.getValue(WeatherEntity.class);
                            tempList.add(entity);
                        }

                        Collections.reverse(tempList);
                        weatherEntities.addAll(tempList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("FirebaseError", "Error fetching weather data", databaseError.toException());
                    }
                });

        return weatherEntities;
    }


    public WeatherEntity getWeatherByCity(String city) {
        Log.d("firebaseCheck","Firebase mai check kr rha");
        final WeatherEntity[] weatherEntity = new WeatherEntity[1];
        try {
            Task<DataSnapshot> snapshotTask = databaseReference.child("weatherData").child(city.toLowerCase()).get();
            DataSnapshot snapshot = Tasks.await(snapshotTask);
            weatherEntity[0] = snapshot.getValue(WeatherEntity.class);
        } catch ( Exception ec) {
            Log.e("firebaseError", "ExecutionException: Error while fetching data from Firebase"+ec);
        }
        return weatherEntity[0];
    }

    public void insertWeather(WeatherEntity weatherEntity, WeatherResponse weatherResponse, ApiResponseCallbackClass.ApiResponseCallback responseCallback) {
        databaseReference.child("weatherData").child(weatherEntity.cityName.toLowerCase())
                .setValue(weatherEntity)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        WeatherData weatherDt = getWeatherDataFromWeatherResponse(weatherResponse,System.currentTimeMillis());
                        Log.d("insertSucc",weatherEntity.timestamp+" ");
                        responseCallback.onSuccess(weatherDt);
                    } else {
                        responseCallback.onFailure("Error saving data to Firebase");
                    }
                });
    }

    public void fetchWeatherFromApi(String city, Context context, ApiResponseCallbackClass.ApiResponseCallback callback) {
        if (!NetworkUtils.isInternetAvailable(context)) {
            callback.onFailure("No internet connection! Please check your network.");
            return;
        }

        weatherApi.getWeather(city, APIConstants.API_KEY).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.d("response","got the response");
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("response","got the successfull response");
                    WeatherResponse weatherResponse = response.body();
                    WeatherData weatherDt = getWeatherDataFromWeatherResponse(weatherResponse,System.currentTimeMillis());
                    WeatherEntity weatherEntity = new WeatherEntity(city.toLowerCase(), weatherDt, System.currentTimeMillis());
//                    Log.d("inserting time",weatherEntity.timestamp+" "+ System.currentTimeMillis());
                    insertWeather(weatherEntity, weatherResponse, callback);
                } else {
                    handleErrorResponse(response, callback);
                    callback.onFailure("Error fetching data");
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
                callback.onFailure("City not found. Please check the city name!!");
                break;
            case 401:
                callback.onFailure("Unauthorized. Check your API key!!");
                break;
            case 500:
                callback.onFailure("Server error. Please try again later!!");
                break;
            default:
                callback.onFailure("Error fetching data: " + response.message());
                break;
        }
    }

    private WeatherData getWeatherDataFromWeatherResponse(WeatherResponse weatherResponse,long timestamp) {
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
                pressure,
                timestamp
        );
    }
}

