package com.suraj.weatherapplication;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WeatherDetail extends AppCompatActivity {
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


        String[] weatherData = (String[]) getIntent().getSerializableExtra("WEATHER_DATA");


        if (weatherData != null) {

            cityNameTextView.setText(weatherData[0]); // City name
            weatherDescriptionTextView.setText(weatherData[1].substring(0, 1).toUpperCase() + weatherData[1].substring(1)); // Weather description
            temperatureTextView.setText("Temperature "+weatherData[2]+" °C"); // Temperature
            feelsLikeTextView.setText("Feels Like "+weatherData[3]+" °C"); // Feels Like
            humidityTextView.setText("Humidity\n"+weatherData[4]+" %"); // Humidity
            pressureTextView.setText("Pressure\n"+weatherData[5]+" Pa"); // Pressure
        }
    }
}

