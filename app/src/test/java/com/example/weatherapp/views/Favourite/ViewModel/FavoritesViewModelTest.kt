package com.example.weatherapp.views.Favourite.ViewModel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.models.FavoriteLocation
import com.example.weatherapp.repo.fake.FakeFavoriteLocationsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesViewModelTest {

    private lateinit var fakeRepository: FakeFavoriteLocationsRepository
    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setup() {
        fakeRepository = FakeFavoriteLocationsRepository()
        viewModel = FavoritesViewModel(fakeRepository)
    }

    @Test
    fun init_collectsFavoriteLocations() = runTest {
        // Given
        val testLocations = listOf(
            FavoriteLocation(
                id = 1,
                latitude = 30.0444,
                longitude = 31.2357,
                cityNameAr = "القاهرة",
                cityNameEn = "Cairo"
            )
        )
        fakeRepository.setFakeLocations(testLocations)

        // When
        val result = viewModel.favoriteLocations.first()

        // Then
        assertThat(result, `is`(testLocations))
    }

    @Test
    fun addFavoriteLocation_addsNewLocation() = runTest {
        // Given
        val testLocation = FavoriteLocation(
            latitude = 30.0444,
            longitude = 31.2357,
            cityNameAr = "القاهرة",
            cityNameEn = "Cairo"
        )

        // When
        viewModel.addFavoriteLocation(testLocation)

        // Then
        val locations = fakeRepository.getAllFavoriteLocations().first()
        assertThat(locations.contains(testLocation), `is`(true))
    }

    @Test
    fun removeFavoriteLocation_removesExistingLocation() = runTest {
        // Given
        val testLocation = FavoriteLocation(
            id = 1,
            latitude = 30.0444,
            longitude = 31.2357,
            cityNameAr = "القاهرة",
            cityNameEn = "Cairo"
        )
        fakeRepository.setFakeLocations(listOf(testLocation))

        // When
        viewModel.removeFavoriteLocation(testLocation)

        // Then
        val locations = fakeRepository.getAllFavoriteLocations().first()
        assertThat(locations.contains(testLocation), `is`(false))
    }

    @Test
    fun updateFavoriteLocation_updatesExistingLocation() = runTest {
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
        fakeRepository.setFakeLocations(listOf(originalLocation))

        // When
        viewModel.updateFavoriteLocation(updatedLocation)

        // Then
        val locations = fakeRepository.getAllFavoriteLocations().first()
        val foundLocation = locations.find { it.id == originalLocation.id }
        assertThat(foundLocation, `is`(updatedLocation))
    }
}