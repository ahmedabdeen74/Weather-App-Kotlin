package com.example.weatherapp.views.Home.ViewModel

import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.repo.WeatherRepository
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {

    var weatherState by mutableStateOf<WeatherState>(WeatherState.Loading)
        private set

    var locationName by mutableStateOf<String>("Loading...")
        private set

    fun fetchWeatherData(lat: Double, lon: Double, geocoder: Geocoder) {
        viewModelScope.launch {
            try {
                // Update location name using Geocoder
                try {
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        locationName = address.locality ?: address.subAdminArea ?:
                                address.adminArea ?: "Unknown Location"
                    }
                } catch (e: IOException) {
                    locationName = "Location Unavailable"
                }

                // Fetch weather data
                val response = repository.getWeatherData(lat, lon)
                if (response.isSuccessful) {
                    response.body()?.let {
                        weatherState = WeatherState.Success(it)
                    } ?: run {
                        weatherState = WeatherState.Error("Empty response")
                    }
                } else {
                    weatherState = WeatherState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                weatherState = WeatherState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
