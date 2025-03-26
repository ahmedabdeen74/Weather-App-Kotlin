package com.example.weatherapp.views.Favourite.ViewModel

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.FavoriteLocation
import com.example.weatherapp.repo.FavoriteLocationsRepositoryImpl
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class FavoritesViewModel(
    private val repository: FavoriteLocationsRepositoryImpl,
    private val context: Context
) : ViewModel() {

    private val _favoriteLocations = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val favoriteLocations = _favoriteLocations.asStateFlow()

    private val _searchQuery = MutableSharedFlow<String>(replay = 0)
    val searchQuery = _searchQuery.asSharedFlow()

    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions = _searchSuggestions.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllFavoriteLocations().collect { locations ->
                _favoriteLocations.value = locations
            }
        }

        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isNotEmpty()) {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        try {
                            val addresses = geocoder.getFromLocationName(query, 5)
                            val suggestions = addresses?.map { it.locality ?: it.featureName } ?: emptyList()
                            _searchSuggestions.value = suggestions
                        } catch (e: Exception) {
                            _searchSuggestions.value = emptyList()
                        }
                    } else {
                        _searchSuggestions.value = emptyList()
                    }
                }
        }
    }

    fun addFavoriteLocation(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.addToFavorites(location)
        }
    }

    fun removeFavoriteLocation(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.removeFromFavorites(location)
        }
    }

    fun searchLocation(query: String) {
        viewModelScope.launch {
            _searchQuery.emit(query)
        }
    }

    suspend fun getCityNameFromLatLng(latLng: LatLng): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            addresses?.firstOrNull()?.let { address ->
                address.locality ?: address.featureName ?: address.adminArea ?: "Unknown Location"
            } ?: "Unknown Location"
        } catch (e: Exception) {
            "Unknown Location"
        }
    }
}