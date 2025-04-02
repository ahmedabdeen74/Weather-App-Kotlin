package com.example.weatherapp.views.Map


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.repo.favourite.FavoriteLocationsRepositoryImpl

class MapSelectionViewModelFactory(
    private val repository: FavoriteLocationsRepositoryImpl,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapSelectionViewModel::class.java)) {
            return MapSelectionViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}