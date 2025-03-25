
package com.example.weatherapp.data.local

import com.example.weatherapp.models.FavoriteLocation
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>
    suspend fun addToFavorites(location: FavoriteLocation)
    suspend fun removeFromFavorites(location: FavoriteLocation)
    suspend fun isLocationFavorite(locationId: Int): Boolean
}