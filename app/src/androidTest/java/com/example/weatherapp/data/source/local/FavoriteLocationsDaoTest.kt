package com.example.weatherapp.data.source.local


import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherapp.data.local.favourite.FavoriteLocationsDao
import com.example.weatherapp.data.local.favourite.FavoriteLocationsDatabase
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
@SmallTest
class FavoriteLocationsDaoTest {
    private lateinit var database: FavoriteLocationsDatabase
    private lateinit var dao: FavoriteLocationsDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FavoriteLocationsDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.favoriteLocationsDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertFavoriteLocation_shouldBeRetrievable() = runTest {
        // Given
        val location = FavoriteLocation(
            latitude = 30.0444,
            longitude = 31.2357,
            cityNameEn = "Cairo",
            cityNameAr = "القاهرة"
        )

        // When
        dao.insertFavoriteLocation(location)
        val allLocations = dao.getAllFavoriteLocations().first()

        // Then
        assertThat(allLocations.size, `is`(1))
        assertThat(allLocations[0].latitude, `is`(location.latitude))
        assertThat(allLocations[0].longitude, `is`(location.longitude))
        assertThat(allLocations[0].cityNameEn, `is`(location.cityNameEn))
        assertThat(allLocations[0].cityNameAr, `is`(location.cityNameAr))
    }

    @Test
    fun deleteFavoriteLocation_shouldRemoveFromDatabase() = runTest {
        // Given
        val location = FavoriteLocation(
            id = 1,
            latitude = 30.0444,
            longitude = 31.2357,
            cityNameEn = "Cairo"
        )
        dao.insertFavoriteLocation(location)

        // When
        dao.deleteFavoriteLocation(location)
        val allLocations = dao.getAllFavoriteLocations().first()

        // Then
        assertThat(allLocations.size, `is`(0))
    }

    @Test
    fun updateFavoriteLocation_shouldUpdateExistingRecord() = runTest {
        // Given
        val originalLocation = FavoriteLocation(
            id = 1,
            latitude = 30.0444,
            longitude = 31.2357,
            cityNameEn = "Cairo",
            cityNameAr = "القاهرة"
        )
        dao.insertFavoriteLocation(originalLocation)

        // When
        val updatedLocation = originalLocation.copy(
            cityNameEn = "New Cairo",
            cityNameAr = "القاهرة الجديدة"
        )
        dao.updateFavoriteLocation(updatedLocation)
        val allLocations = dao.getAllFavoriteLocations().first()

        // Then
        assertThat(allLocations.size, `is`(1))
        assertThat(allLocations[0].cityNameEn, `is`("New Cairo"))
        assertThat(allLocations[0].cityNameAr, `is`("القاهرة الجديدة"))
        assertThat(allLocations[0].latitude, `is`(originalLocation.latitude))
        assertThat(allLocations[0].longitude, `is`(originalLocation.longitude))
    }

    @Test
    fun insertDuplicateLocation_shouldReplaceExisting() = runTest {
        // Given
        val location1 = FavoriteLocation(
            id = 1,
            latitude = 30.0444,
            longitude = 31.2357,
            cityNameEn = "Cairo"
        )
        dao.insertFavoriteLocation(location1)

        // When
        val location2 = FavoriteLocation(
            id = 1, // Same ID
            latitude = 29.9855,
            longitude = 31.4404,
            cityNameEn = "Giza"
        )
        dao.insertFavoriteLocation(location2)
        val allLocations = dao.getAllFavoriteLocations().first()

        // Then
        assertThat(allLocations.size, `is`(1))
        assertThat(allLocations[0].cityNameEn, `is`("Giza"))
        assertThat(allLocations[0].latitude, `is`(29.9855))
    }
}