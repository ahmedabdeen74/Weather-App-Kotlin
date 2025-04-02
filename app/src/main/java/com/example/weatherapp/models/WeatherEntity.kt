package com.example.weatherapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val weatherResponse: WeatherResponse,
    val locationName: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "forecast")
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val forecastResponse: ForecastResponse
)