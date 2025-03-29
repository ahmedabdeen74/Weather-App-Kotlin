package com.example.weatherapp.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.weatherapp.models.WeatherAlert

@Database(entities = [WeatherAlert::class], version = 3, exportSchema = false)
abstract class WeatherAlertsDatabase : RoomDatabase() {

    abstract fun weatherAlertsDao(): WeatherAlertsDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherAlertsDatabase? = null

        fun getInstance(context: Context): WeatherAlertsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherAlertsDatabase::class.java,
                    "weather_alerts_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}