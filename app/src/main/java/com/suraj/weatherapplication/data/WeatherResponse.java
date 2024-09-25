package com.suraj.weatherapplication.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WeatherResponse implements Serializable {
    @SerializedName("coord")
    private Coord coord;

    @SerializedName("weather")
    private Weather[] weather;

    @SerializedName("main")
    private Main main;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("clouds")
    private Clouds clouds;

    @SerializedName("sys")
    private Sys sys;

    @SerializedName("name")
    private String name;

    // Getters for each field
    public Coord getCoord() { return coord; }
    public Weather[] getWeather() { return weather; }
    public  Main getMain() { return main; }
    public Wind getWind() { return wind; }
    public Clouds getClouds() { return clouds; }
    public Sys getSys() { return sys; }
    public String getName() { return name; }

    public static class Weather {
        @SerializedName("description")
        private String description;

        public String getDescription() {
            return description;
        }
    }

    public static class Coord {
        @SerializedName("lon")
        private double lon;

        @SerializedName("lat")
        private double lat;

        public double getLon() { return lon; }
        public double getLat() { return lat; }
    }

    public static class Main {
        @SerializedName("temp")
        private double temp;

        @SerializedName("feels_like")
        private double feelsLike;

        @SerializedName("humidity")
        private int humidity;

        @SerializedName("pressure")
        long pressure;

        public double getTemp() { return temp; }
        public double getFeelsLike() { return feelsLike; }
        public int getHumidity() { return humidity; }
        public long getPressure(){ return pressure;}
    }

    public static class Wind {
        @SerializedName("speed")
        private static double speed;

        @SerializedName("deg")
        private int deg;

        public static double getSpeed() { return speed; }
        public int getDeg() { return deg; }
    }

    public static class Clouds {
        @SerializedName("all")
        private int all;

        public int getAll() { return all; }
    }

    public static class Sys {
        @SerializedName("country")
        private String country;

        public String getCountry() { return country; }
    }
}

