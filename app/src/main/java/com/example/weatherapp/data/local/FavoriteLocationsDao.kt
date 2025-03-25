package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.weatherapp.models.FavoriteLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteLocationsDao {
    @Query("SELECT * FROM favorite_locations")
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>

    @Insert
    suspend fun insertFavoriteLocation(location: FavoriteLocation)

    @Delete
    suspend fun deleteFavoriteLocation(location: FavoriteLocation)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_locations WHERE id = :locationId)")
    suspend fun isLocationFavorite(locationId: Int): Boolean
}