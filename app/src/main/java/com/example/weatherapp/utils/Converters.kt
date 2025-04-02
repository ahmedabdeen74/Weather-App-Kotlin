package com.example.weatherapp.utils

import androidx.room.TypeConverter
import com.example.weatherapp.models.ForecastItem
import com.example.weatherapp.models.ForecastResponse
import com.example.weatherapp.models.Weather
import com.example.weatherapp.models.WeatherResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromWeatherList(weatherList: List<Weather>?): String {
        return gson.toJson(weatherList)
    }

    @TypeConverter
    fun toWeatherList(weatherString: String): List<Weather> {
        val listType = object : TypeToken<List<Weather>>() {}.type
        return gson.fromJson(weatherString, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromForecastItemList(forecastItems: List<ForecastItem>?): String {
        return gson.toJson(forecastItems)
    }

    @TypeConverter
    fun toForecastItemList(forecastString: String): List<ForecastItem> {
        val listType = object : TypeToken<List<ForecastItem>>() {}.type
        return gson.fromJson(forecastString, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromWeatherResponse(weatherResponse: WeatherResponse?): String {
        return gson.toJson(weatherResponse)
    }

    @TypeConverter
    fun toWeatherResponse(weatherResponseString: String): WeatherResponse? {
        val type = object : TypeToken<WeatherResponse>() {}.type
        return gson.fromJson(weatherResponseString, type)
    }

    @TypeConverter
    fun fromForecastResponse(forecastResponse: ForecastResponse?): String {
        return gson.toJson(forecastResponse)
    }

    @TypeConverter
    fun toForecastResponse(forecastResponseString: String): ForecastResponse? {
        val type = object : TypeToken<ForecastResponse>() {}.type
        return gson.fromJson(forecastResponseString, type)
    }
}