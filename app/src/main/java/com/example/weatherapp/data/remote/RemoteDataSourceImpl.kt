package com.example.weatherapp.data.remote

import com.example.weatherapp.models.ForecastResponse
import com.example.weatherapp.models.WeatherResponse
import retrofit2.Response

class RemoteDataSourceImpl : RemoteDataSource {
    private val apiService = RetrofitHelper.getInstance().create(WeatherApiService::class.java)
    private val apiKey = "d2a8546c718e7b95e2aeb91a5d8ecffb"

    override suspend fun getWeatherData(lat: Double, lon: Double): Response<WeatherResponse> {
        return apiService.getWeatherData(lat, lon, apiKey)
    }
    override suspend fun getForecastData(lat: Double, lon: Double): Response<ForecastResponse> {
        return apiService.getForecastData(lat, lon, apiKey)
    }

}