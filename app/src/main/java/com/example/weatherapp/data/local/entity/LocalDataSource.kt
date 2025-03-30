package com.example.weatherapp.data.local.entity


import com.example.weatherapp.data.local.entity.ForecastEntity
import com.example.weatherapp.data.local.entity.WeatherEntity

interface LocalDataSource {
    suspend fun saveWeather(weather: WeatherEntity)
    suspend fun saveForecast(forecast: ForecastEntity)
    suspend fun getWeather(): WeatherEntity?
    suspend fun getForecast(): ForecastEntity?
    suspend fun clearWeather()
    suspend fun clearForecast()
}