package com.suraj.weatherapplication.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
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
    private TextView logoutTextview,recentsTextView;



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
        logoutTextview=activityMainBinding.logoutTextView;
        recentsTextView=activityMainBinding.textViewRecents;


        CheckWeatherFactory factory = new CheckWeatherFactory(getApplication());
        checkWeatherViewModel = new ViewModelProvider(this, factory).get(CheckWeatherViewModel.class);

        changeRecentsTextColorUsingRemoteConfig();




        checkWeatherViewModel.getAllWeatherData().observe(this, weatherEntities -> {

//            Log.d("Suraj", "data changed" + weatherEntities.get(0).getCityName());
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

        logoutTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Snackbar.make(view, "Logged out successfully!", Snackbar.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,SignupLoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });
    }


    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void changeRecentsTextColorUsingRemoteConfig()
    {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(60)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            String recentResult=mFirebaseRemoteConfig.getString("Recent_color");
                            recentsTextView.setTextColor(Color.parseColor(recentResult));

                        } else {
                            Snackbar.make(findViewById(android.R.id.content), "Fetch failed",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}



