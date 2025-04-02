package com.example.weatherapp.data.local.alerts

import com.example.weatherapp.models.WeatherAlert
import kotlinx.coroutines.flow.Flow


class WeatherAlertsLocalDataSourceImpl(
    private val weatherAlertsDao: WeatherAlertsDao
) : WeatherAlertsLocalDataSource {
    override fun getAllWeatherAlerts(): Flow<List<WeatherAlert>> {
        return weatherAlertsDao.getAllWeatherAlerts()
    }

    override suspend fun addWeatherAlert(alert: WeatherAlert) {
        weatherAlertsDao.insertWeatherAlert(alert)
    }

    override suspend fun deleteWeatherAlert(alert: WeatherAlert) {
        weatherAlertsDao.deleteWeatherAlert(alert)
    }

    override suspend fun updateAlertStatus(alertId: String, isActive: Boolean) {
        weatherAlertsDao.updateAlertStatus(alertId, isActive)
    }
}