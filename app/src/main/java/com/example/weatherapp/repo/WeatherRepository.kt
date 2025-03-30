package com.example.weatherapp.repo

import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.remote.RemoteDataSource
import com.example.weatherapp.models.ForecastResponse
import com.example.weatherapp.models.WeatherResponse
import retrofit2.Response

interface WeatherRepository {
    suspend fun getWeatherData(lat: Double, lon: Double): Response<WeatherResponse>
    suspend fun getForecastData(lat: Double, lon: Double): Response<ForecastResponse>
    suspend fun saveWeather(weather: WeatherResponse, locationName: String)
    suspend fun saveForecast(forecast: ForecastResponse)
    suspend fun getLocalWeather(): WeatherResponse?
    suspend fun getLocalForecast(): ForecastResponse?
    suspend fun getLocalLocationName(): String?
    suspend fun getLastUpdated(): Long?
}