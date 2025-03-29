package com.example.weatherapp.views.WeatherAlerts.viewModel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.AlertType
import com.example.weatherapp.models.WeatherAlert
import com.example.weatherapp.repo.WeatherAlertsRepository
import com.example.weatherapp.utils.AlarmReceiver
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherAlertsViewModel(
    private val context: Context,
    private val repository: WeatherAlertsRepository
) : ViewModel() {

    private val _alerts = MutableStateFlow<List<WeatherAlert>>(emptyList())
    val alerts: StateFlow<List<WeatherAlert>> = _alerts

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val deletedAlerts: MutableList<WeatherAlert> = mutableListOf()

    init {
        viewModelScope.launch {
            repository.getAllWeatherAlerts().collect { alerts ->
                _alerts.value = alerts
            }
        }
    }

    fun addWeatherAlert(triggerTime: Long, alertType: AlertType) {
        viewModelScope.launch {
            val alert = WeatherAlert(triggerTime = triggerTime, alertType = alertType)
            repository.addWeatherAlert(alert)
            scheduleAlarm(alert)
        }
    }

    fun deleteAlert(alertId: String) {
        viewModelScope.launch {
            val alert = _alerts.value.find { it.id == alertId }
            alert?.let {
                Log.d("WeatherAlertsViewModel", "Deleting alert: ${it.id}, isActive: ${it.isActive}, type: ${it.alertType}")
                // Add the alert to the deletedAlerts list only if it is active
                if (it.isActive) {
                    deletedAlerts.add(it)
                    Log.d("WeatherAlertsViewModel", "Alert added to deletedAlerts, count: ${deletedAlerts.size}")
                } else {
                    Log.d("WeatherAlertsViewModel", "Alert is expired, not added to deletedAlerts")
                }
                repository.deleteWeatherAlert(it)
                cancelAlarm(it)
            }
        }
    }

    suspend fun undoDeleteAlert(): Boolean {
        val deferred = CompletableDeferred<Boolean>()
        viewModelScope.launch {
            if (deletedAlerts.isNotEmpty()) {
                val lastDeletedAlert = deletedAlerts.removeAt(deletedAlerts.size - 1)
                val currentTime = System.currentTimeMillis()
                Log.d("WeatherAlertsViewModel", "Undoing delete for alert: ${lastDeletedAlert.id}, type: ${lastDeletedAlert.alertType}")
                Log.d("WeatherAlertsViewModel", "triggerTime: ${lastDeletedAlert.triggerTime}, currentTime: $currentTime")
                Log.d("WeatherAlertsViewModel", "triggerTime <= currentTime: ${lastDeletedAlert.triggerTime <= currentTime}")
                val updatedAlert = if (lastDeletedAlert.triggerTime <= currentTime) {
                    lastDeletedAlert.copy(isActive = false)
                } else {
                    lastDeletedAlert.copy(isActive = true)
                }
                Log.d("WeatherAlertsViewModel", "Updated alert isActive: ${updatedAlert.isActive}")
                repository.addWeatherAlert(updatedAlert)
                if (updatedAlert.isActive && updatedAlert.triggerTime > currentTime) {
                    scheduleAlarm(updatedAlert)
                    Log.d("WeatherAlertsViewModel", "Alarm rescheduled for restored alert: ${updatedAlert.id}")
                }
                deferred.complete(updatedAlert.isActive) // Return the isActive status
            } else {
                deferred.complete(false) // If there are no deleted alerts
            }
        }
        return deferred.await()
    }

    fun getDeletedAlertsCount(): Int {
        return deletedAlerts.size
    }

    private fun scheduleAlarm(alert: WeatherAlert) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alertId", alert.id)
            putExtra("alertType", alert.alertType.name)
            putExtra("triggerTime", alert.triggerTime)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alert.triggerTime,
            pendingIntent
        )
    }

    private fun cancelAlarm(alert: WeatherAlert) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}

