
package com.example.weatherapp.repo


import com.example.weatherapp.models.FavoriteLocation
import kotlinx.coroutines.flow.Flow



import com.example.weatherapp.data.local.LocalDataSource


class FavoriteLocationsRepositoryImpl private constructor(
    private val localDataSource: LocalDataSource
) : FavoriteLocationsRepository {

    override fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>> {
        return localDataSource.getAllFavoriteLocations()
    }

    override suspend fun addToFavorites(location: FavoriteLocation) {
        localDataSource.addToFavorites(location)
    }

    override suspend fun removeFromFavorites(location: FavoriteLocation) {
        localDataSource.removeFromFavorites(location)
    }

    override suspend fun isLocationFavorite(locationId: Int): Boolean {
        return localDataSource.isLocationFavorite(locationId)
    }

    companion object {
        @Volatile
        private var instance: FavoriteLocationsRepositoryImpl? = null

        fun getInstance(localDataSource: LocalDataSource): FavoriteLocationsRepositoryImpl {
            return instance ?: synchronized(this) {
                instance ?: FavoriteLocationsRepositoryImpl(localDataSource).also {
                    instance = it
                }
            }
        }
    }
}