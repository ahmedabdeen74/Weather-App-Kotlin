package com.example.weatherapp.views.Home.ViewModel


import com.example.weatherapp.models.WeatherResponse

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val data: WeatherResponse) : WeatherState()
    data class Error(val message: String) : WeatherState()
}
sealed class ForecastState {
    object Loading : ForecastState()
    data class Success(val data: List<ForecastDisplay>) : ForecastState()
    data class Error(val message: String) : ForecastState()
}
