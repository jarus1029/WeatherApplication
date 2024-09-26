package com.suraj.weatherapplication.view.activity;

import android.health.connect.datatypes.units.Temperature;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.suraj.weatherapplication.R;
import com.suraj.weatherapplication.data.CONSTANTS;
import com.suraj.weatherapplication.databinding.ActivityMainBinding;
import com.suraj.weatherapplication.databinding.ActivityWeatherDetailBinding;
import com.suraj.weatherapplication.model.WeatherData;

public class WeatherDetailActivity extends AppCompatActivity {
    private TextView cityNameTextView;
    private TextView weatherDescriptionTextView;
    private TextView temperatureTextView;
    private TextView feelsLikeTextView;
    private TextView humidityTextView;
    private TextView pressureTextView;
    ActivityWeatherDetailBinding activityWeatherDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        activityWeatherDetailBinding= activityWeatherDetailBinding.inflate(getLayoutInflater());
        View view=activityWeatherDetailBinding.getRoot();
        setContentView(view);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        cityNameTextView=activityWeatherDetailBinding.cityNameTextView;
        weatherDescriptionTextView=activityWeatherDetailBinding.weatherDescriptionTextView;
        temperatureTextView=activityWeatherDetailBinding.temperatureTextView;
        feelsLikeTextView=activityWeatherDetailBinding.feelsLikeTextView;
        humidityTextView=activityWeatherDetailBinding.humidityTextView;
        pressureTextView=activityWeatherDetailBinding.pressureTextView;


        WeatherData weatherData = (WeatherData) getIntent().getParcelableExtra(CONSTANTS.INTENTEXTRANAME);


        if (weatherData != null) {

            cityNameTextView.setText(weatherData.getCityName()); // City name
            weatherDescriptionTextView.setText(weatherData.getWeatherDescription().substring(0, 1).toUpperCase() + weatherData.getWeatherDescription().substring(1));// Weather description
            temperatureTextView.setText(getResources().getString(R.string.weather_detail, CONSTANTS.TEMPERATURE)+String.format("%.2f", weatherData.getTemperature())+" °C");
            feelsLikeTextView.setText(getResources().getString(R.string.weather_detail, CONSTANTS.FEELS_LIKE)+String.format("%.2f", weatherData.getFeelsLike())+" °C"); // Feels Like
            humidityTextView.setText(getResources().getString(R.string.weather_detail, CONSTANTS.HUMIDITY)+weatherData.getHumidity()+" %"); // Humidity
            pressureTextView.setText(getResources().getString(R.string.weather_detail, CONSTANTS.PRESSURE)+weatherData.getPressure()+" Pa"); // Pressure
        }
    }
}

