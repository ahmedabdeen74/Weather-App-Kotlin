package com.example.weatherapp.repo.favourite

import com.example.weatherapp.models.FavoriteLocation
import com.example.weatherapp.data.source.FakeFavoriteLocationsDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class FavoriteLocationsRepositoryImplTest {

    private lateinit var fakeDataSource: FakeFavoriteLocationsDataSource
    private lateinit var repository: FavoriteLocationsRepositoryImpl

    @Before
    fun setup() {
        fakeDataSource = FakeFavoriteLocationsDataSource()
        repository = FavoriteLocationsRepositoryImpl(fakeDataSource)
    }

    @Test
    fun addToFavorites_locationAdded_success() = runTest {
        // Given
        val testLocation = FavoriteLocation(
            latitude = 30.0444,
            longitude = 31.2357,
            cityNameAr = "القاهرة",
            cityNameEn = "Cairo"
        )

        // When
        repository.addToFavorites(testLocation)

        // Then
        val locations = fakeDataSource.getAllFavoriteLocations().first()
        assertThat(locations.contains(testLocation), `is`(true))
    }

    @Test
    fun removeFromFavorites_locationRemoved_success() = runTest {
        // Given
        val testLocation = FavoriteLocation(
            latitude = 30.0444,
            longitude = 31.2357,
            cityNameAr = "القاهرة",
            cityNameEn = "Cairo"
        )
        fakeDataSource.addToFavorites(testLocation)

        // When
        repository.removeFromFavorites(testLocation)

        // Then
        val locations = fakeDataSource.getAllFavoriteLocations().first()
        assertThat(locations.contains(testLocation), `is`(false))
    }

    @Test
    fun updateFavoriteLocation_locationUpdated_success() = runTest {
        // Given
        val originalLocation = FavoriteLocation(
            id = 1,
            latitude = 30.0444,
            longitude = 31.2357,
            cityNameAr = "القاهرة",
            cityNameEn = "Cairo"
        )

        val updatedLocation = originalLocation.copy(
            cityNameAr = "القاهرة الجديدة",
            cityNameEn = "New Cairo"
        )

        // Add the original location
        repository.addToFavorites(originalLocation)

        // When
        repository.updateFavoriteLocation(updatedLocation)

        // Then
        val locations = fakeDataSource.getAllFavoriteLocations().first()
        val foundLocation = locations.find { it.id == originalLocation.id }

        assertThat(foundLocation, `is`(updatedLocation))
        assertThat(foundLocation?.cityNameAr, `is`("القاهرة الجديدة"))
        assertThat(foundLocation?.cityNameEn, `is`("New Cairo"))
    }
}