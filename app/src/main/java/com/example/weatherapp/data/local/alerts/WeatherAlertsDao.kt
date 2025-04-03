package com.example.weatherapp.data.local.alerts


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.weatherapp.models.WeatherAlert
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherAlertsDao {
    @Query("SELECT * FROM weather_alerts")
    fun getAllWeatherAlerts(): Flow<List<WeatherAlert>>

    @Insert
    suspend fun insertWeatherAlert(alert: WeatherAlert)

    @Delete
    suspend fun deleteWeatherAlert(alert: WeatherAlert)

    @Query("UPDATE weather_alerts SET isActive = :isActive WHERE id = :alertId")
    suspend fun updateAlertStatus(alertId: String, isActive: Boolean)

    @Update
    suspend fun updateWeatherAlert(alert: WeatherAlert)
}