
package com.example.weatherapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_locations")
data class FavoriteLocation(
    @PrimaryKey val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double
)