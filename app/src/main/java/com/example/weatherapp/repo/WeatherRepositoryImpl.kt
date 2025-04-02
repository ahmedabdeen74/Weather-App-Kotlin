package com.example.weatherapp.repo

import com.example.weatherapp.data.local.entity.ForecastEntity
import com.example.weatherapp.data.local.entity.LocalDataSource
import com.example.weatherapp.data.local.entity.WeatherEntity
import com.example.weatherapp.data.remote.RemoteDataSource
import com.example.weatherapp.models.ForecastResponse
import com.example.weatherapp.models.WeatherResponse
import retrofit2.Response

class WeatherRepositoryImpl private constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : WeatherRepository {

    override suspend fun getWeatherData(lat: Double, lon: Double, lang: String): Response<WeatherResponse> {
        return remoteDataSource.getWeatherData(lat, lon, lang)
    }

    override suspend fun getForecastData(lat: Double, lon: Double, lang: String): Response<ForecastResponse> {
        return remoteDataSource.getForecastData(lat, lon, lang)
    }

    override suspend fun saveWeather(weather: WeatherResponse, locationName: String) {
        val weatherEntity = WeatherEntity(
            weatherResponse = weather,
            locationName = locationName,
            lastUpdated = System.currentTimeMillis()
        )
        localDataSource.clearWeather()
        localDataSource.saveWeather(weatherEntity)
    }

    override suspend fun saveForecast(forecast: ForecastResponse) {
        val forecastEntity = ForecastEntity(
            forecastResponse = forecast
        )
        localDataSource.clearForecast()
        localDataSource.saveForecast(forecastEntity)
    }

    override suspend fun getLocalWeather(): WeatherResponse? {
        val weatherEntity = localDataSource.getWeather() ?: return null
        return weatherEntity.weatherResponse
    }

    override suspend fun getLocalForecast(): ForecastResponse? {
        val forecastEntity = localDataSource.getForecast() ?: return null
        return forecastEntity.forecastResponse
    }

    override suspend fun getLocalLocationName(): String? {
        return localDataSource.getWeather()?.locationName
    }

    override suspend fun getLastUpdated(): Long? {
        return localDataSource.getWeather()?.lastUpdated
    }

    companion object {
        @Volatile
        private var instance: WeatherRepositoryImpl? = null

        fun getInstance(remoteDataSource: RemoteDataSource, localDataSource: LocalDataSource): WeatherRepositoryImpl {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepositoryImpl(remoteDataSource, localDataSource).also {
                    instance = it
                }
            }
        }
    }
}