package com.example.weatherapp.data.local.weather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.models.ForecastEntity
import com.example.weatherapp.models.WeatherEntity

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: ForecastEntity)

    @Query("SELECT * FROM weather LIMIT 1")
    suspend fun getWeather(): WeatherEntity?

    @Query("SELECT * FROM forecast LIMIT 1")
    suspend fun getForecast(): ForecastEntity?

    @Query("DELETE FROM weather")
    suspend fun clearWeather()

    @Query("DELETE FROM forecast")
    suspend fun clearForecast()
}