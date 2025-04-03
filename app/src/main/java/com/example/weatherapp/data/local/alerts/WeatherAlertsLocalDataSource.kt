package com.example.weatherapp.data.local.alerts

import com.example.weatherapp.models.WeatherAlert
import kotlinx.coroutines.flow.Flow

interface WeatherAlertsLocalDataSource {
    fun getAllWeatherAlerts(): Flow<List<WeatherAlert>>
    suspend fun addWeatherAlert(alert: WeatherAlert)
    suspend fun deleteWeatherAlert(alert: WeatherAlert)
    suspend fun updateAlertStatus(alertId: String, isActive: Boolean)
    suspend fun updateWeatherAlert(alert: WeatherAlert)
}