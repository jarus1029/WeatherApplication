package com.suraj.weatherapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.suraj.weatherapplication.api.APIConstants;
import com.suraj.weatherapplication.dao.WeatherApi;
import com.suraj.weatherapplication.database.AppDatabase;
import com.suraj.weatherapplication.dto.WeatherResponse;
import com.suraj.weatherapplication.entity.WeatherEntity;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private EditText citySearch;
    private Button searchButton;
    private AppDatabase db;
    private WeatherApi weatherApi;
    RecyclerView recyclerView;
    WeatherAdapter weatherAdapter;
    private ProgressBar progressBar;
    private ConstraintLayout mainLayout;


    List<WeatherEntity> allWeatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        citySearch = findViewById(R.id.citySearch);
        searchButton = findViewById(R.id.searchButton);
//        weatherInfo = findViewById(R.id.weatherInfo);
        db = AppDatabase.getDatabase(this);

        recyclerView=findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        progressBar = findViewById(R.id.progressBar);
        mainLayout = findViewById(R.id.innerLayout);



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherApi = retrofit.create(WeatherApi.class);

        searchButton.setOnClickListener(v -> {
            String city = citySearch.getText().toString().trim();
            if (!city.isEmpty()) {
                checkWeather(city);
            }
            else
            {
                Snackbar.make(findViewById(android.R.id.content), "Please enter the city name !!!", Snackbar.LENGTH_SHORT).show();
            }
        });
        loadAllWeatherData();
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }


        if (mainLayout != null) {
            mainLayout.setVisibility(View.VISIBLE);
        }
    }

    private void checkWeather(final String city) {
        String formattedCity=city.toLowerCase();
        new Thread(() -> {
            Log.d("DBCheck", "Looking for city: " + formattedCity);
            WeatherEntity weatherEntity = db.weatherDao().getWeatherByCity(formattedCity);
            long currentTime = System.currentTimeMillis();

            if (weatherEntity != null) {
                Log.d("DBCheck", "Found in DB: " + weatherEntity.weatherData);
                Log.d("time-testing",currentTime - weatherEntity.timestamp+"");
                Log.d("time-testing",currentTime+" "+ weatherEntity.timestamp);
                if ((currentTime - weatherEntity.timestamp) < (4 * 60 * 60 * 1000)) {
                    Log.d("test", "into less than 4 hours");
                    runOnUiThread(() ->{
                                Intent intent = new Intent(MainActivity.this, WeatherDetail.class);
                                intent.putExtra("WEATHER_DATA", weatherEntity.weatherData);
                                startActivity(intent);
                                loadAllWeatherData();
                            }
                    );
                } else {
                    Log.d("test", "into greater than 4 hours");
                    fetchWeatherFromApi(formattedCity);
                }
            } else {
                Log.d("DBCheck", "No data found, fetching from API.");
                fetchWeatherFromApi(formattedCity);
            }
        }).start();
    }


    private void fetchWeatherFromApi(String city) {
        String formattedCity=city.toLowerCase();
        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
        });

        if (!isInternetAvailable()) {
            runOnUiThread(() ->
                    Snackbar.make(findViewById(android.R.id.content), "No internet connection! Please check your network.", Snackbar.LENGTH_LONG).show());
//                    progressBar.setVisibility(View.GONE);
//                    mainLayout.setVisibility(View.VISIBLE);
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
            });
            return;
        }


        weatherApi.getWeather(formattedCity, APIConstants.API_KEY).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                runOnUiThread(() -> {

                });


                if (response.isSuccessful()) {

                    Log.d("test","into fetching from api");
                    WeatherResponse weatherResponse = response.body();
                    String weatherDescription = weatherResponse.getWeather().length > 0 ? weatherResponse.getWeather()[0].getDescription() : "No data";
                    double temperature = weatherResponse.getMain().getTemp()-273.15;
                    double feelsLike = weatherResponse.getMain().getFeelsLike()-273.15;
                    int humidity = weatherResponse.getMain().getHumidity();
                    long pressure=weatherResponse.getMain().getPressure();
                    String cityName = weatherResponse.getName();

                    String tempStr = "" + temperature;
                    String feelsLikeStr = "" + feelsLike;

                    String[] weatherData = new String[]{
                            cityName,
                            weatherDescription,
                            tempStr.length() >= 5 ? tempStr.substring(0, 5) : tempStr,
                            feelsLikeStr.length() >= 5 ? feelsLikeStr.substring(0, 5) : feelsLikeStr,
                            ""+humidity,
                            (""+pressure)
                    };


                    WeatherEntity weatherEntity = new WeatherEntity(formattedCity, weatherData, System.currentTimeMillis());

                    new Thread(() -> {
                        Log.d("test","into inserting into db");
                        db.weatherDao().insertWeather(weatherEntity);
                        logAllWeatherData();
                        runOnUiThread(() -> {
                                    Intent intent = new Intent(MainActivity.this, WeatherDetail.class);
                                    intent.putExtra("WEATHER_DATA", weatherData);
                                    startActivity(intent);
                                    loadAllWeatherData();
                                }
                        );
                    }).start();

                } else {

                    switch (response.code()) {
                        case 404:
                            Log.e("apiissue", "Error 404: City not found. Please check the city name.");
                            runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content), "City not found. Please check the city name !!", Snackbar.LENGTH_SHORT).show());
                            break;
                        case 401:
                            Log.e("apiissue", "Error 401: Unauthorized. Check your API key.");
                            runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content), "Unauthorized. Check your API key !!", Snackbar.LENGTH_SHORT).show());
                            break;
                        case 500:
                            Log.e("apiissue", "Error 500: Server error. Please try again later.");
                            runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content), "Server error. Please try again later !!", Snackbar.LENGTH_SHORT).show());
                            break;
                        default:
                            try {
                                Log.e("apiissue", "Error: " + response.errorBody().string());
                                runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content), "Error fetching data: "+response.errorBody().toString(), Snackbar.LENGTH_SHORT).show());
                            } catch (IOException e) {
                                e.printStackTrace();
                                runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content), "An unexpected error occurred.", Snackbar.LENGTH_SHORT).show());
                            }
                            break;
                    }

                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                t.printStackTrace();
                runOnUiThread(() ->{
                            Snackbar.make(findViewById(android.R.id.content), "Error fetching data: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                );
            }

        });
    }

    private void logAllWeatherData() {
        new Thread(() -> {
            List<WeatherEntity> allWeatherData = db.weatherDao().getAllWeatherData();
            for (WeatherEntity entity : allWeatherData) {
                Log.d("DBData", "City: " + entity.cityName + ", Data: " + entity.weatherData+", TIme"+entity.timestamp);
            }
        }).start();
    }

    public void readWeatherDataAsync() {
        new Thread(() -> {
            List<WeatherEntity> weatherData = db.weatherDao().getAllWeatherData();
            runOnUiThread(() -> {
                allWeatherData = weatherData;
                weatherAdapter = new WeatherAdapter(allWeatherData, this, new WeatherAdapter.ItemClicked() {
                    @Override
                    public void onClick(int position, View view) {
                        WeatherEntity selectedCityWeather = weatherData.get(position);  // Get data for clicked city

                        Intent intent = new Intent(MainActivity.this, WeatherDetail.class);
                        intent.putExtra("WEATHER_DATA", selectedCityWeather.getWeatherData());  // Pass weather data array
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(weatherAdapter);
            });
        }).start();
    }


    private void loadAllWeatherData() {
        readWeatherDataAsync();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        } else {
            android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }



}
