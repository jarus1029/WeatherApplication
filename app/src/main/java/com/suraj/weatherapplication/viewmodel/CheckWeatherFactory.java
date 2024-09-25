package com.suraj.weatherapplication.viewmodel;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CheckWeatherFactory implements ViewModelProvider.Factory {
    private final Application application;

    public CheckWeatherFactory(Application application) {
        this.application = application;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CheckWeatherViewModel.class)) {
            return (T) new CheckWeatherViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
