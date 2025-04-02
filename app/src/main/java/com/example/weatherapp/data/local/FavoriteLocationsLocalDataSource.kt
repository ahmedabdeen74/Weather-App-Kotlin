package com.example.weatherapp.data.local


import com.example.weatherapp.models.FavoriteLocation
import kotlinx.coroutines.flow.Flow


class FavoriteLocationsLocalDataSource(
    private val favoriteLocationsDao: FavoriteLocationsDao
) : LocalDataSource {
    override fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>> {
        return favoriteLocationsDao.getAllFavoriteLocations()
    }

    override suspend fun addToFavorites(location: FavoriteLocation) {
        favoriteLocationsDao.insertFavoriteLocation(location)
    }

    override suspend fun removeFromFavorites(location: FavoriteLocation) {
        favoriteLocationsDao.deleteFavoriteLocation(location)
    }

    override suspend fun updateFavoriteLocation(location: FavoriteLocation) {
        favoriteLocationsDao.updateFavoriteLocation(location)
    }

    override suspend fun isLocationFavorite(locationId: Int): Boolean {
        return favoriteLocationsDao.isLocationFavorite(locationId)
    }
}