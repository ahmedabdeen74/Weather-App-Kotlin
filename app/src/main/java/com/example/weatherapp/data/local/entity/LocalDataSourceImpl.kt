package com.example.weatherapp.data.local.entity


import com.example.weatherapp.data.local.dao.WeatherDao
import com.example.weatherapp.data.local.entity.ForecastEntity
import com.example.weatherapp.data.local.entity.WeatherEntity

class LocalDataSourceImpl(private val weatherDao: WeatherDao) : LocalDataSource {
    override suspend fun saveWeather(weather: WeatherEntity) {
        weatherDao.insertWeather(weather)
    }

    override suspend fun saveForecast(forecast: ForecastEntity) {
        weatherDao.insertForecast(forecast)
    }

    override suspend fun getWeather(): WeatherEntity? {
        return weatherDao.getWeather()
    }

    override suspend fun getForecast(): ForecastEntity? {
        return weatherDao.getForecast()
    }

    override suspend fun clearWeather() {
        weatherDao.clearWeather()
    }

    override suspend fun clearForecast() {
        weatherDao.clearForecast()
    }
}