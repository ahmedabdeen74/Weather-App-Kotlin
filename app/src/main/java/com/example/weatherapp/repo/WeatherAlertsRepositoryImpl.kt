package com.example.weatherapp.repo



import com.example.weatherapp.data.local.WeatherAlertsLocalDataSource
import com.example.weatherapp.models.WeatherAlert
import kotlinx.coroutines.flow.Flow

class WeatherAlertsRepositoryImpl private constructor(
    private val localDataSource: WeatherAlertsLocalDataSource
) : WeatherAlertsRepository {

    override fun getAllWeatherAlerts(): Flow<List<WeatherAlert>> {
        return localDataSource.getAllWeatherAlerts()
    }

    override suspend fun addWeatherAlert(alert: WeatherAlert) {
        localDataSource.addWeatherAlert(alert)
    }

    override suspend fun deleteWeatherAlert(alert: WeatherAlert) {
        localDataSource.deleteWeatherAlert(alert)
    }

    override suspend fun updateAlertStatus(alertId: String, isActive: Boolean) {
        localDataSource.updateAlertStatus(alertId, isActive)
    }

    companion object {
        @Volatile
        private var instance: WeatherAlertsRepositoryImpl? = null

        fun getInstance(localDataSource: WeatherAlertsLocalDataSource): WeatherAlertsRepositoryImpl {
            return instance ?: synchronized(this) {
                instance ?: WeatherAlertsRepositoryImpl(localDataSource).also {
                    instance = it
                }
            }
        }
    }
}