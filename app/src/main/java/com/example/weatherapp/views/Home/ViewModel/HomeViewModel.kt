package com.example.weatherapp.views.Home.ViewModel

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.ForecastItem
import com.example.weatherapp.repo.WeatherRepository
import com.example.weatherapp.utils.NetworkUtils
import com.example.weatherapp.views.Settings.TemperatureUnit
import com.example.weatherapp.views.Settings.WindSpeedUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HomeViewModel(
    private val repository: WeatherRepository,
    private val context: Context
) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()

    private val _forecastState = MutableStateFlow<ForecastState>(ForecastState.Loading)
    val forecastState: StateFlow<ForecastState> = _forecastState.asStateFlow()

    private val _locationName = MutableStateFlow("Loading...")
    val locationName: StateFlow<String> = _locationName.asStateFlow()

    private val _temperatureUnit = MutableStateFlow(TemperatureUnit.CELSIUS)
    val temperatureUnit = _temperatureUnit.asStateFlow()

    private val _windSpeedUnit = MutableStateFlow(WindSpeedUnit.METER_PER_SEC)
    val windSpeedUnit = _windSpeedUnit.asStateFlow()

    private val _locationSource = MutableStateFlow(LocationSource.GPS)
    val locationSource: StateFlow<LocationSource> = _locationSource.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _lastUpdated = MutableStateFlow<Long?>(null)
    val lastUpdated: StateFlow<Long?> = _lastUpdated.asStateFlow()

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)

    fun fetchWeatherData(lat: Double, lon: Double, geocoder: Geocoder) {
        viewModelScope.launch {
            _isOnline.value = NetworkUtils.isNetworkAvailable(context)
            if (_isOnline.value) {
                // Network is available, fetch from API
                try {
                    // Fetch location name
                    try {
                        val addresses = geocoder.getFromLocation(lat, lon, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0]
                            _locationName.value = address.locality ?: address.subAdminArea ?: address.adminArea ?: "Unknown Location"
                        }
                    } catch (e: Exception) {
                        _locationName.value = "Location Unavailable"
                    }

                    // Fetch weather data
                    val response = repository.getWeatherData(lat, lon)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _weatherState.value = WeatherState.Success(it)
                            repository.saveWeather(it, _locationName.value) // Save to local database with lastUpdated timestamp
                            // Save the weather description to SharedPreferences
                            val weatherDescription = it.weather.firstOrNull()?.description?.capitalize() ?: "Unknown"
                            sharedPreferences.edit()
                                .putString("weather_description", weatherDescription)
                                .putString("location_name", _locationName.value)
                                .apply()
                        } ?: run {
                            _weatherState.value = WeatherState.Error("Empty response")
                        }
                    } else {
                        _weatherState.value = WeatherState.Error("Error: ${response.code()}")
                    }

                    fetchForecastData(lat, lon)
                } catch (e: Exception) {
                    _weatherState.value = WeatherState.Error(e.message ?: "Unknown error")
                }
            } else {
                // Network is unavailable, fetch from local database
                val localWeather = repository.getLocalWeather()
                val localLocationName = repository.getLocalLocationName()
                val lastUpdatedTime = repository.getLastUpdated()
                if (localWeather != null && localLocationName != null) {
                    _weatherState.value = WeatherState.Success(localWeather)
                    _locationName.value = localLocationName
                    _lastUpdated.value = lastUpdatedTime // Set the last updated time for the UI
                } else {
                    _weatherState.value = WeatherState.Error("No internet and no local data available")
                }

                val localForecast = repository.getLocalForecast()
                if (localForecast != null) {
                    val forecastItems = localForecast.list.map { forecastItem ->
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val date = dateFormat.parse(forecastItem.dt_txt)
                        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                        val timeFormat = SimpleDateFormat("h a", Locale.getDefault())

                        ForecastDisplay(
                            day = dayFormat.format(date!!),
                            time = timeFormat.format(date),
                            icon = forecastItem.weather.firstOrNull()?.icon ?: "01d",
                            temp = forecastItem.main.temp.toInt(),
                            description = forecastItem.weather.firstOrNull()?.description ?: "unknown"
                        )
                    }
                    _forecastState.value = ForecastState.Success(forecastItems)
                } else {
                    _forecastState.value = ForecastState.Error("No internet and no local forecast data available")
                }
            }
        }
    }

    private fun fetchForecastData(lat: Double, lon: Double) {
        viewModelScope.launch {
            if (NetworkUtils.isNetworkAvailable(context)) {
                try {
                    val response = repository.getForecastData(lat, lon)
                    if (response.isSuccessful) {
                        response.body()?.let { forecastResponse ->
                            repository.saveForecast(forecastResponse) // Save to local database
                            val forecastItems = forecastResponse.list.map { forecastItem ->
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                val date = dateFormat.parse(forecastItem.dt_txt)
                                val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                                val timeFormat = SimpleDateFormat("h a", Locale.getDefault())

                                ForecastDisplay(
                                    day = dayFormat.format(date!!),
                                    time = timeFormat.format(date),
                                    icon = forecastItem.weather.firstOrNull()?.icon ?: "01d",
                                    temp = forecastItem.main.temp.toInt(),
                                    description = forecastItem.weather.firstOrNull()?.description ?: "unknown"
                                )
                            }
                            _forecastState.value = ForecastState.Success(forecastItems)
                        } ?: run {
                            _forecastState.value = ForecastState.Error("Empty forecast response")
                        }
                    } else {
                        _forecastState.value = ForecastState.Error("Error fetching forecast: ${response.code()}")
                    }
                } catch (e: Exception) {
                    _forecastState.value = ForecastState.Error(e.message ?: "Unknown forecast error")
                }
            }
        }
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        _temperatureUnit.value = unit
    }

    fun convertTemperature(celsius: Double): Double {
        return when (_temperatureUnit.value) {
            TemperatureUnit.CELSIUS -> celsius
            TemperatureUnit.KELVIN -> celsius + 273.15
            TemperatureUnit.FAHRENHEIT -> (celsius * 9/5) + 32
        }
    }

    fun setWindSpeedUnit(unit: WindSpeedUnit) {
        _windSpeedUnit.value = unit
    }

    fun convertWindSpeed(meterPerSec: Double): Double {
        return when (_windSpeedUnit.value) {
            WindSpeedUnit.METER_PER_SEC -> meterPerSec
            WindSpeedUnit.MILE_PER_HOUR -> meterPerSec * 2.23694
        }
    }

    fun setLocationSource(source: LocationSource) {
        _locationSource.value = source
    }
}

enum class LocationSource {
    GPS, MAP
}

data class ForecastDisplay(
    val day: String,
    val time: String,
    val icon: String,
    val temp: Int,
    val description: String
)