package com.suraj.weatherapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class WeatherData implements Parcelable {
    private String cityName;
    private String weatherDescription;
    private double temperature;
    private double feelsLike;
    private int humidity;
    private long pressure;

    // Constructor
    public WeatherData(String cityName, String weatherDescription, double temperature, double feelsLike, int humidity, long pressure) {
        this.cityName = cityName;
        this.weatherDescription = weatherDescription;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.pressure = pressure;
    }

    // Parcelable implementation
    protected WeatherData(Parcel in) {
        cityName = in.readString();
        weatherDescription = in.readString();
        temperature = in.readDouble();
        feelsLike = in.readDouble();
        humidity = in.readInt();
        pressure = in.readLong();
    }

    public static final Creator<WeatherData> CREATOR = new Creator<WeatherData>() {
        @Override
        public WeatherData createFromParcel(Parcel in) {
            return new WeatherData(in);
        }

        @Override
        public WeatherData[] newArray(int size) {
            return new WeatherData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cityName);
        dest.writeString(weatherDescription);
        dest.writeDouble(temperature);
        dest.writeDouble(feelsLike);
        dest.writeInt(humidity);
        dest.writeLong(pressure);
    }

    // Getters and setters...
    public String getCityName() {
        return cityName;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public int getHumidity() {
        return humidity;
    }

    public long getPressure() {
        return pressure;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "cityName='" + cityName + '\'' +
                ", weatherDescription='" + weatherDescription + '\'' +
                ", temperature=" + temperature +
                ", feelsLike=" + feelsLike +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                '}';
    }
}
