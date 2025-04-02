package com.example.weatherapp.data.local.weather


import com.example.weatherapp.models.ForecastEntity
import com.example.weatherapp.models.WeatherEntity

interface LocalDataSource {
    suspend fun saveWeather(weather: WeatherEntity)
    suspend fun saveForecast(forecast: ForecastEntity)
    suspend fun getWeather(): WeatherEntity?
    suspend fun getForecast(): ForecastEntity?
    suspend fun clearWeather()
    suspend fun clearForecast()
}