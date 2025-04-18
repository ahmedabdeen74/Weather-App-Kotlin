package com.example.weatherapp.data.remote

import com.example.weatherapp.models.ForecastResponse
import com.example.weatherapp.models.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String
    ): Response<WeatherResponse>

    @GET("data/2.5/forecast")
    suspend fun getForecastData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String
    ): Response<ForecastResponse>
}