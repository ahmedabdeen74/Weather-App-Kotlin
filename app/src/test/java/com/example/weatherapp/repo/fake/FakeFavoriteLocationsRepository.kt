package com.example.weatherapp.repo.fake

import com.example.weatherapp.models.FavoriteLocation
import com.example.weatherapp.repo.favourite.FavoriteLocationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeFavoriteLocationsRepository : FavoriteLocationsRepository {

    private val _favoriteLocations = MutableStateFlow<List<FavoriteLocation>>(emptyList())

    fun setFakeLocations(locations: List<FavoriteLocation>) {
        _favoriteLocations.value = locations
    }

    override fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>> {
        return _favoriteLocations
    }

    override suspend fun addToFavorites(location: FavoriteLocation) {
        _favoriteLocations.update { current ->
            if (current.any { it.id == location.id }) {
                current
            } else {
                current + location
            }
        }
    }

    override suspend fun removeFromFavorites(location: FavoriteLocation) {
        _favoriteLocations.update { current ->
            current.filterNot { it.id == location.id }
        }
    }

    override suspend fun updateFavoriteLocation(location: FavoriteLocation) {
        _favoriteLocations.update { current ->
            current.map { if (it.id == location.id) location else it }
        }
    }
}