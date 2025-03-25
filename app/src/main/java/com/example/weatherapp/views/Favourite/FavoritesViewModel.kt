/*
package com.example.weatherapp.views.Favourite


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.FavoriteLocation
import com.example.weatherapp.repo.FavoriteLocationsRepositoryImpl
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: FavoriteLocationsRepositoryImpl
) : ViewModel() {

    private val _favoriteLocations = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val favoriteLocations = _favoriteLocations.asStateFlow()

    private val _searchQuery = MutableSharedFlow<String>()
    val searchQuery = _searchQuery.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.getAllFavoriteLocations().collect() { locations ->
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

    fun searchLocation(query: String) {
        viewModelScope.launch {
            _searchQuery.emit(query)
        }
    }
}


 */