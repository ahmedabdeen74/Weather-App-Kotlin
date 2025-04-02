package com.example.weatherapp.views.Favourite.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.FavoriteLocation
import com.example.weatherapp.repo.favourite.FavoriteLocationsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: FavoriteLocationsRepositoryImpl
) : ViewModel() {

    private val _favoriteLocations = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val favoriteLocations = _favoriteLocations.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllFavoriteLocations().collect { locations ->
                _favoriteLocations.value = locations
            }
        }
    }

    fun addFavoriteLocation(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.addToFavorites(location)
        }
    }

    fun removeFavoriteLocation(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.removeFromFavorites(location)
        }
    }

    fun updateFavoriteLocation(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.updateFavoriteLocation(location)
        }
    }
}