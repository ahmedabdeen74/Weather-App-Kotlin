package com.example.weatherapp.views.Home.ViewModel

import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.ForecastItem
import com.example.weatherapp.repo.WeatherRepository
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {

    var weatherState by mutableStateOf<WeatherState>(WeatherState.Loading)
        private set

    var forecastState by mutableStateOf<ForecastState>(ForecastState.Loading)
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
                        locationName = address.locality ?: address.subAdminArea ?: address.adminArea
                                ?: "Unknown Location"
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

                // Fetch forecast data
                fetchForecastData(lat, lon)

            } catch (e: Exception) {
                weatherState = WeatherState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun fetchForecastData(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = repository.getForecastData(lat, lon)
                if (response.isSuccessful) {
                    response.body()?.let { forecastResponse ->
                        val forecastItems = forecastResponse.list.map { forecastItem ->
                            val dateFormat =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val date = dateFormat.parse(forecastItem.dt_txt)

                            val dayFormat =
                                SimpleDateFormat("EEEE", Locale.getDefault())
                            val timeFormat =
                                SimpleDateFormat("h a", Locale.getDefault())

                            ForecastDisplay(
                                day = dayFormat.format(date!!),
                                time = timeFormat.format(date),
                                icon = forecastItem.weather.firstOrNull()?.icon ?: "01d",
                                temp = forecastItem.main.temp.toInt(),
                                description = forecastItem.weather.firstOrNull()?.description ?: "unknown"
                            )
                        }
                        forecastState = ForecastState.Success(forecastItems)
                    } ?: run {
                        forecastState = ForecastState.Error("Empty forecast response")
                    }
                } else {
                    forecastState =
                        ForecastState.Error("Error fetching forecast: ${response.code()}")
                }
            } catch (e: Exception) {
                forecastState = ForecastState.Error(e.message ?: "Unknown forecast error")
            }
        }
    }

}
// Add a new data class for simplified forecast display
data class ForecastDisplay(
    val day: String,
    val time: String,
    val icon: String,
    val temp: Int,
    val description: String
)

