package com.example.weatherapp.views.WeatherAlerts.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.repo.WeatherAlertsRepository

class WeatherAlertsViewModelFactory(
    private val context: Context,
    private val repository: WeatherAlertsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherAlertsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherAlertsViewModel(context, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}