package com.example.weatherapp.views.Home.ViewModel

import com.example.weatherapp.models.WeatherResponse

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val data: WeatherResponse) : WeatherState()
    data class Error(val message: String) : WeatherState()
}