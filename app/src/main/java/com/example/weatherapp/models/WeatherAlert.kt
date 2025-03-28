package com.example.weatherapp.models

import java.util.UUID

enum class AlertType {
    NOTIFICATION, SOUND
}

data class WeatherAlert(
    val id: String = UUID.randomUUID().toString(),
    val triggerTime: Long,
    val alertType: AlertType,
    val isActive: Boolean = true
)