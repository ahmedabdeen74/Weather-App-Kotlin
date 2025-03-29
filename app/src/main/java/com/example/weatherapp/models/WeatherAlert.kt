package com.example.weatherapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "weather_alerts")
data class WeatherAlert(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val triggerTime: Long,
    val alertType: AlertType,
    val isActive: Boolean = true
)

enum class AlertType {
    NOTIFICATION,
    ALARM,
}