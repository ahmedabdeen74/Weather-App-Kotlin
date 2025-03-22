package com.example.weatherapp.repo

import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.data.remote.RemoteDataSource
import com.example.weatherapp.models.ForecastResponse
import retrofit2.Response

class WeatherRepositoryImpl private constructor(
    private val remoteDataSource: RemoteDataSource
) : WeatherRepository {

    override suspend fun getWeatherData(lat: Double, lon: Double): Response<WeatherResponse> {
        return remoteDataSource.getWeatherData(lat, lon)
    }
    override suspend fun getForecastData(lat: Double, lon: Double): Response<ForecastResponse> {
        return remoteDataSource.getForecastData(lat, lon)
    }



    companion object {
        @Volatile
        private var instance: WeatherRepositoryImpl? = null

        fun getInstance(remoteDataSource: RemoteDataSource): WeatherRepositoryImpl {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepositoryImpl(remoteDataSource).also {
                    instance = it
                }
            }
        }
    }
}