
package com.example.weatherapp.repo.favourite

import com.example.weatherapp.models.FavoriteLocation
import kotlinx.coroutines.flow.Flow
interface FavoriteLocationsRepository {
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>
    suspend fun addToFavorites(location: FavoriteLocation)
    suspend fun removeFromFavorites(location: FavoriteLocation)
    suspend fun updateFavoriteLocation(location: FavoriteLocation)
}