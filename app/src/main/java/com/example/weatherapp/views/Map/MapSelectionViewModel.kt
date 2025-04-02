package com.example.weatherapp.views.Map

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.FavoriteLocation
import com.example.weatherapp.repo.favourite.FavoriteLocationsRepositoryImpl
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

class MapSelectionViewModel(
    private val repository: FavoriteLocationsRepositoryImpl,
    private val context: Context
) : ViewModel() {

    private val _searchQuery = MutableSharedFlow<String>(replay = 0)
    val searchQuery = _searchQuery.asSharedFlow()

    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions = _searchSuggestions.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isNotEmpty()) {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        try {
                            val addresses = geocoder.getFromLocationName(query, 5)
                            val suggestions = addresses?.mapNotNull {
                                it.locality ?: it.featureName ?: it.adminArea
                            }?.distinct() ?: emptyList()
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

    fun searchLocation(query: String) {
        viewModelScope.launch {
            _searchQuery.emit(query)
        }
    }


}