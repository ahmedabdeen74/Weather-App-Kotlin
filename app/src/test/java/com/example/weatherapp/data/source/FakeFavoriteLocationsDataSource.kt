package com.example.weatherapp.data.source

import com.example.weatherapp.data.local.favourite.LocalDataSource
import com.example.weatherapp.models.FavoriteLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeFavoriteLocationsDataSource : LocalDataSource {
    private val _favoriteLocations = MutableStateFlow<List<FavoriteLocation>>(emptyList())

    override fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>> {
        return _favoriteLocations
    }

    override suspend fun addToFavorites(location: FavoriteLocation) {
        val currentLocations = _favoriteLocations.value.toMutableList()
        if (!currentLocations.contains(location)) {
            currentLocations.add(location)
            _favoriteLocations.value = currentLocations
        }
    }

    override suspend fun removeFromFavorites(location: FavoriteLocation) {
        val currentLocations = _favoriteLocations.value.toMutableList()
        currentLocations.remove(location)
        _favoriteLocations.value = currentLocations
    }

    override suspend fun isLocationFavorite(locationId: Int): Boolean {
        return _favoriteLocations.value.any { it.id == locationId }
    }

    override suspend fun updateFavoriteLocation(location: FavoriteLocation) {
        val currentLocations = _favoriteLocations.value.toMutableList()
        val index = currentLocations.indexOfFirst { it.id == location.id }
        if (index != -1) {
            currentLocations[index] = location
            _favoriteLocations.value = currentLocations
        }
    }
}