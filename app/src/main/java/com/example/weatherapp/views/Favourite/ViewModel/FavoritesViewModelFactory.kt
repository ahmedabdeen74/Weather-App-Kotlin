package com.example.weatherapp.views.Favourite.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.repo.FavoriteLocationsRepositoryImpl

class FavoritesViewModelFactory(
    private val repository: FavoriteLocationsRepositoryImpl,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            return FavoritesViewModel(repository,context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}