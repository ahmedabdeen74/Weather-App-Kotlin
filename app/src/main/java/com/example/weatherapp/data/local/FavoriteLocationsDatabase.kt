package com.example.weatherapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.models.FavoriteLocation

@Database(entities = [FavoriteLocation::class], version = 2)
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
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}