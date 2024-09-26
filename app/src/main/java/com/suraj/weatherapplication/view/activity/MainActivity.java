package com.suraj.weatherapplication.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.suraj.weatherapplication.R;
import com.suraj.weatherapplication.data.CONSTANTS;
import com.suraj.weatherapplication.databinding.ActivityMainBinding;
import com.suraj.weatherapplication.model.WeatherData;
import com.suraj.weatherapplication.view.adapter.WeatherAdapter;
import com.suraj.weatherapplication.model.WeatherEntity;
import com.suraj.weatherapplication.viewmodel.CheckWeatherViewModel;
import com.suraj.weatherapplication.viewmodel.CheckWeatherFactory;

public class MainActivity extends AppCompatActivity {
    private EditText citySearch;
    private CheckWeatherViewModel checkWeatherViewModel;
    private RecyclerView recyclerView;
    private WeatherAdapter weatherAdapter;
    private ProgressBar progressBar;
    private ConstraintLayout mainLayout;
    private ImageView searchButton;

    ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=activityMainBinding.getRoot();
        setContentView(view);


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        citySearch =activityMainBinding.citySearch;
        searchButton = activityMainBinding.searchButton;
        recyclerView = activityMainBinding.recycler;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = activityMainBinding.progressBar;
        mainLayout = activityMainBinding.innerLayout;


        CheckWeatherFactory factory = new CheckWeatherFactory(getApplication());
        checkWeatherViewModel = new ViewModelProvider(this, factory).get(CheckWeatherViewModel.class);


        checkWeatherViewModel.getAllWeatherData().observe(this, weatherEntities -> {

            Log.d("Suraj", "data changed" + weatherEntities.get(0).getCityName());
            weatherAdapter = new WeatherAdapter(weatherEntities, this, new WeatherAdapter.ItemClicked() {
                @Override
                public void onClick(int position, View view) {
                    WeatherEntity selectedCityWeather = weatherEntities.get(position);
                    Intent intent = new Intent(MainActivity.this, WeatherDetailActivity.class);
                    intent.putExtra(CONSTANTS.INTENTEXTRANAME, selectedCityWeather.getWeatherData());
                    startActivity(intent);
                }
            });

            recyclerView.setAdapter(weatherAdapter);

        });



         checkWeatherViewModel.getWeatherData().observe(this, weatherData -> {
             WeatherData weatherDetails = checkWeatherViewModel.getWeatherData().getValue();
             if (weatherDetails != null) {

                 Intent intent = new Intent(MainActivity.this, WeatherDetailActivity.class);
                 intent.putExtra(CONSTANTS.INTENTEXTRANAME, weatherDetails);
                 startActivity(intent);
             }
         });

        checkWeatherViewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
            }
        });

        checkWeatherViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                if (isLoading) {
                    progressBar.setVisibility(View.VISIBLE);
                    mainLayout.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                }
            }
        });


        searchButton.setOnClickListener(v -> {
            String city = citySearch.getText().toString().trim();
            hideKeyboard(v);
            if (!city.isEmpty()) {
                checkWeatherViewModel.checkWeather(city);
            } else {
                Snackbar.make(v, "Please enter a city name!", Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}



