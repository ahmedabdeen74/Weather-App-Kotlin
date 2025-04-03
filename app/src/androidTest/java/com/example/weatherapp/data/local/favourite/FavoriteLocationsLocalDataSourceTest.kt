package com.example.weatherapp.data.local.favourite


import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherapp.models.FavoriteLocation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class FavoriteLocationsLocalDataSourceTest {
    private lateinit var database: FavoriteLocationsDatabase
    private lateinit var dao: FavoriteLocationsDao
    private lateinit var dataSource: FavoriteLocationsLocalDataSource

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FavoriteLocationsDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.favoriteLocationsDao()
        dataSource = FavoriteLocationsLocalDataSource(dao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addToFavorites_shouldInsertLocation() = runTest {
        // Given
        val location = FavoriteLocation(
            latitude = 30.0444,
            longitude = 31.2357,
            cityNameEn = "Cairo",
            cityNameAr = "القاهرة"
        )

        // When
        dataSource.addToFavorites(location)
        val favorites = dataSource.getAllFavoriteLocations().first()

        // Then
        assertThat(favorites.size, `is`(1))
        assertThat(favorites[0].cityNameEn, `is`("Cairo"))
        assertThat(favorites[0].cityNameAr, `is`("القاهرة"))
    }

    @Test
    fun removeFromFavorites_shouldDeleteLocation() = runTest {
        // Given
        val location = FavoriteLocation(
            id = 1,
            latitude = 30.0444,
            longitude = 31.2357,
            cityNameEn = "Cairo"
        )
        dataSource.addToFavorites(location)

        // When
        dataSource.removeFromFavorites(location)
        val favorites = dataSource.getAllFavoriteLocations().first()

        // Then
        assertThat(favorites.size, `is`(0))
    }

    @Test
    fun updateFavoriteLocation_shouldModifyExistingLocation() = runTest {
        // Given
        val originalLocation = FavoriteLocation(
            id = 1,
            latitude = 30.0444,
            longitude = 31.2357,
            cityNameEn = "Cairo"
        )
        dataSource.addToFavorites(originalLocation)

        // When
        val updatedLocation = originalLocation.copy(
            cityNameEn = "New Cairo",
            latitude = 30.0275,
            longitude = 31.4921
        )
        dataSource.updateFavoriteLocation(updatedLocation)
        val favorites = dataSource.getAllFavoriteLocations().first()

        // Then
        assertThat(favorites.size, `is`(1))
        assertThat(favorites[0].cityNameEn, `is`("New Cairo"))
        assertThat(favorites[0].latitude, `is`(30.0275))
        assertThat(favorites[0].longitude, `is`(31.4921))
    }

    @Test
    fun isLocationFavorite_shouldReturnCorrectStatus() = runTest {
        // Given
        val location = FavoriteLocation(
            id = 1,
            latitude = 30.0444,
            longitude = 31.2357,
            cityNameEn = "Cairo"
        )
        dataSource.addToFavorites(location)

        // When & Then
        assertThat(dataSource.isLocationFavorite(1), `is`(true))
        assertThat(dataSource.isLocationFavorite(2), `is`(false))
    }
}