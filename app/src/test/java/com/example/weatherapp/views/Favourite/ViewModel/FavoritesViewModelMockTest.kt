package com.example.weatherapp.views.Favourite.ViewModel


import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.models.FavoriteLocation
import com.example.weatherapp.repo.favourite.FavoriteLocationsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesViewModelMockTest {

    private lateinit var mockRepository: FavoriteLocationsRepository
    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setup() {
        mockRepository = mockk(relaxed = true)
        viewModel = FavoritesViewModel(mockRepository)
    }

    @Test
    fun `addFavoriteLocation should call repository addToFavorites`() = runTest {
        // Given
        val testLocation = FavoriteLocation(
            latitude = 30.0444,
            longitude = 31.2357,
            cityNameAr = "القاهرة",
            cityNameEn = "Cairo"
        )
        coEvery { mockRepository.addToFavorites(testLocation) } returns Unit

        // When
        viewModel.addFavoriteLocation(testLocation)

        // Then
        coVerify { mockRepository.addToFavorites(testLocation) }
    }

    @Test
    fun `removeFavoriteLocation should call repository removeFromFavorites`() = runTest {
        // Given
        val testLocation = FavoriteLocation(
            id = 1,
            latitude = 30.0444,
            longitude = 31.2357,
            cityNameAr = "القاهرة",
            cityNameEn = "Cairo"
        )
        coEvery { mockRepository.removeFromFavorites(testLocation) } returns Unit

        // When
        viewModel.removeFavoriteLocation(testLocation)

        // Then
        coVerify { mockRepository.removeFromFavorites(testLocation) }
    }

    @Test
    fun `updateFavoriteLocation should call repository updateFavoriteLocation`() = runTest {
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
        coEvery { mockRepository.updateFavoriteLocation(updatedLocation) } returns Unit

        // When
        viewModel.updateFavoriteLocation(updatedLocation)

        // Then
        coVerify { mockRepository.updateFavoriteLocation(updatedLocation) }
    }
}