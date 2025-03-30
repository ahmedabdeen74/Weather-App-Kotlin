package com.example.weatherapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherapp.models.ForecastResponse
import com.example.weatherapp.models.WeatherResponse

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