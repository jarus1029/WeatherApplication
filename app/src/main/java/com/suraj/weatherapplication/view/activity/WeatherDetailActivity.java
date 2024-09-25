package com.suraj.weatherapplication.view.activity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.suraj.weatherapplication.R;
import com.suraj.weatherapplication.model.WeatherData;

public class WeatherDetailActivity extends AppCompatActivity {
    private TextView cityNameTextView;
    private TextView weatherDescriptionTextView;
    private TextView temperatureTextView;
    private TextView feelsLikeTextView;
    private TextView humidityTextView;
    private TextView pressureTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        cityNameTextView = findViewById(R.id.cityNameTextView);
        weatherDescriptionTextView = findViewById(R.id.weatherDescriptionTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        feelsLikeTextView = findViewById(R.id.feelsLikeTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        pressureTextView = findViewById(R.id.pressureTextView);


        WeatherData weatherData = (WeatherData) getIntent().getParcelableExtra("WEATHER_DATA");


        if (weatherData != null) {

            cityNameTextView.setText(weatherData.getCityName()); // City name
            weatherDescriptionTextView.setText(weatherData.getWeatherDescription().substring(0, 1).toUpperCase() + weatherData.getWeatherDescription().substring(1)); // Weather description
            temperatureTextView.setText("Temperature "+String.format("%.2f", weatherData.getTemperature())+" °C"); // Temperature
            feelsLikeTextView.setText("Feels Like "+String.format("%.2f", weatherData.getFeelsLike())+" °C"); // Feels Like
            humidityTextView.setText("Humidity\n"+weatherData.getHumidity()+" %"); // Humidity
            pressureTextView.setText("Pressure\n"+weatherData.getPressure()+" Pa"); // Pressure
        }
    }
}

