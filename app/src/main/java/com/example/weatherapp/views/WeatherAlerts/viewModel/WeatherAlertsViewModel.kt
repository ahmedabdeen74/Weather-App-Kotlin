package com.example.weatherapp.views.WeatherAlerts.viewModel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.AlertType
import com.example.weatherapp.models.WeatherAlert
import com.example.weatherapp.utils.AlarmReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherAlertsViewModel(private val context: Context) : ViewModel() {

    private val _alerts = MutableStateFlow<List<WeatherAlert>>(emptyList())
    val alerts: StateFlow<List<WeatherAlert>> = _alerts

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun addWeatherAlert(triggerTime: Long, alertType: AlertType) {
        viewModelScope.launch {
            val alert = WeatherAlert(triggerTime = triggerTime, alertType = alertType)
            val currentAlerts = _alerts.value.toMutableList()
            currentAlerts.add(alert)
            _alerts.value = currentAlerts

            scheduleAlarm(alert)
        }
    }

    fun disableAlert(alertId: String) {
        viewModelScope.launch {
            val currentAlerts = _alerts.value.toMutableList()
            val alert = currentAlerts.find { it.id == alertId }
            alert?.let {
                val updatedAlert = it.copy(isActive = false)
                currentAlerts[currentAlerts.indexOf(it)] = updatedAlert
                _alerts.value = currentAlerts

                cancelAlarm(it)
            }
        }
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

class WeatherAlertsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherAlertsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherAlertsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}