
package com.example.weatherapp.repo.favourite


import com.example.weatherapp.models.FavoriteLocation
import kotlinx.coroutines.flow.Flow



import com.example.weatherapp.data.local.favourite.LocalDataSource


class FavoriteLocationsRepositoryImpl  constructor(
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
    override suspend fun updateFavoriteLocation(location: FavoriteLocation) {
        localDataSource.updateFavoriteLocation(location)
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