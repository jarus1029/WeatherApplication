package com.suraj.weatherapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suraj.weatherapplication.entity.WeatherEntity;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherHolder> {

    List<WeatherEntity> weatherData;
    Context context;
    ItemClicked itemClicked;
    ViewGroup parent;

    public WeatherAdapter(List<WeatherEntity> weatherData, Context context, ItemClicked itemClicked) {
        this.weatherData = weatherData;
        this.context = context;
        this.itemClicked = itemClicked;
    }

    @NonNull
    @Override
    public WeatherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.weather_data_layout,parent,false);
        this.parent=parent;
        return new WeatherHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherHolder holder, int position) {
        holder.cityName.setText(weatherData.get(position).getCityName());
        String[] data = weatherData.get(position).getWeatherData();
        if (data.length > 1) {
            holder.description.setText(data[1]);
        } else {
            holder.description.setText("No description available");
        }

        if (data.length > 2) {
            holder.temp.setText(data[2]+" °C");
        } else {
            holder.temp.setText("NA");
        }


    }

    @Override
    public int getItemCount() {
        return weatherData.size();
    }

    class WeatherHolder extends RecyclerView.ViewHolder{

        TextView cityName;
        TextView description;
        TextView temp;

        public WeatherHolder(@NonNull View itemView) {
            super(itemView);

            cityName=itemView.findViewById(R.id.txt_city_Name);
            description=itemView.findViewById(R.id.txt_weather_description);
            temp=itemView.findViewById(R.id.txt_temp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClicked.onClick(getAdapterPosition(),itemView);
                }
            });
        }
    }

    interface ItemClicked{
        default void onClick(int position, View view)
        {

        }
    }
}
