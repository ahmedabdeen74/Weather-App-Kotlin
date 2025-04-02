package com.example.weatherapp.data.remote

import com.example.weatherapp.models.ForecastResponse
import com.example.weatherapp.models.WeatherResponse
import retrofit2.Response

interface RemoteDataSource {
    suspend fun getWeatherData(lat: Double, lon: Double, lang: String): Response<WeatherResponse>
    suspend fun getForecastData(lat: Double, lon: Double, lang: String): Response<ForecastResponse>
}