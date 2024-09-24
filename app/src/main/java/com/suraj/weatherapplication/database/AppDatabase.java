package com.suraj.weatherapplication.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.suraj.weatherapplication.dao.WeatherDao;
import com.suraj.weatherapplication.entity.Converters;
import com.suraj.weatherapplication.entity.WeatherEntity;

@Database(entities = {WeatherEntity.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract WeatherDao weatherDao();

    private static volatile AppDatabase INSTANCE ;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "weather_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
