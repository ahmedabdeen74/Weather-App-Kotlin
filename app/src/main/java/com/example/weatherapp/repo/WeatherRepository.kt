package com.example.weatherapp.repo


import com.example.weatherapp.models.ForecastResponse
import com.example.weatherapp.models.WeatherResponse
import retrofit2.Response

interface WeatherRepository {
    suspend fun getWeatherData(lat: Double, lon: Double): Response<WeatherResponse>
    suspend fun getForecastData(lat: Double, lon: Double): Response<ForecastResponse>
}