package com.example.weatherapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.models.FavoriteLocation

// Room Database
@Database(entities = [FavoriteLocation::class], version = 1)
abstract class FavoriteLocationsDatabase : RoomDatabase() {
    abstract fun favoriteLocationsDao(): FavoriteLocationsDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteLocationsDatabase? = null

        fun getInstance(context: Context): FavoriteLocationsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteLocationsDatabase::class.java,
                    "favorite_locations_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}