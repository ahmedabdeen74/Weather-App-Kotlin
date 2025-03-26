package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.data.local.FavoriteLocationsDatabase
import com.example.weatherapp.data.local.FavoriteLocationsLocalDataSource
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.repo.FavoriteLocationsRepositoryImpl
import com.example.weatherapp.repo.WeatherRepositoryImpl
import com.example.weatherapp.utils.ScreenRoute
import com.example.weatherapp.views.Favourite.View.FavoritesView
import com.example.weatherapp.views.Favourite.View.MapSelectionView
import com.example.weatherapp.views.Favourite.ViewModel.FavoritesViewModel
import com.example.weatherapp.views.Favourite.ViewModel.FavoritesViewModelFactory
import com.example.weatherapp.views.Home.View.HomeView
import com.example.weatherapp.views.Home.ViewModel.HomeViewModel
import com.example.weatherapp.views.Home.ViewModel.HomeViewModelFactory
import com.example.weatherapp.views.Settings.SettingsView
import com.google.android.gms.location.*
import java.util.Locale

class MainActivity : ComponentActivity() {

    lateinit var navHostController: NavHostController
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var favoritesViewModel: FavoritesViewModel

    // Request location permissions
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            // Permission granted, get location
            getLastLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize HomeViewModel
        val repository = WeatherRepositoryImpl.getInstance(RemoteDataSourceImpl())
        val factory = HomeViewModelFactory(repository)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        // Initialize FavoritesViewModel with Context
        val favoriteLocationsDao = FavoriteLocationsDatabase.getInstance(this).favoriteLocationsDao()
        val localDataSource = FavoriteLocationsLocalDataSource(favoriteLocationsDao)
        val favoritesRepository = FavoriteLocationsRepositoryImpl.getInstance(localDataSource)
        val favoritesViewModelFactory = FavoritesViewModelFactory(favoritesRepository, this)
        favoritesViewModel = ViewModelProvider(this, favoritesViewModelFactory)[FavoritesViewModel::class.java]

        // Request location permissions
        requestLocationPermissions()

        setContent {
            navHostController = rememberNavController()
            SetupNavHost()
        }
    }

    private fun requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            // Permissions already granted
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    // Location retrieved, fetch weather data
                    val geocoder = android.location.Geocoder(this, Locale.getDefault())
                    homeViewModel.fetchWeatherData(location.latitude, location.longitude, geocoder)
                } else {
                    // Last location might be null, request location updates
                    requestLocationUpdates()
                }
            }
        }
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    // Location retrieved, fetch weather data
                    val geocoder = android.location.Geocoder(this@MainActivity, Locale.getDefault())
                    homeViewModel.fetchWeatherData(location.latitude, location.longitude, geocoder)

                    // Remove updates after getting location
                    fusedLocationClient.removeLocationUpdates(this)
                    break
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    @Composable
    fun SetupNavHost() {
        NavHost(
            navController = navHostController,
            startDestination = ScreenRoute.HomeViewRoute.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(ScreenRoute.HomeViewRoute.route) {
                HomeView(
                    viewModel = homeViewModel,
                    onSettingClick = {
                        navHostController.navigate(ScreenRoute.SettingViewRoute.route)
                    },
                    onHomeClick = {
                        navHostController.navigate(ScreenRoute.HomeViewRoute.route) {
                            navHostController.popBackStack()
                        }
                    },
                    onFavoriteClick = {
                        navHostController.navigate(ScreenRoute.FavoritesViewRoute.route)
                    }
                )
            }
            composable(ScreenRoute.SettingViewRoute.route) {
                SettingsView(
                    viewModel = homeViewModel,
                    onBackClick = {
                        navHostController.popBackStack(ScreenRoute.HomeViewRoute.route, inclusive = false)
                    }
                )
            }
            composable(ScreenRoute.FavoritesViewRoute.route) {
                FavoritesView(
                    viewModel = favoritesViewModel,
                    onMapClick = {
                        navHostController.navigate(ScreenRoute.MapSelectionViewRoute.route)
                    },
                    onBackClick = {
                        navHostController.popBackStack(ScreenRoute.HomeViewRoute.route, inclusive = false)
                    }
                )
            }
            composable(ScreenRoute.MapSelectionViewRoute.route) {
                MapSelectionView(
                    viewModel = favoritesViewModel,
                    onBackClick = {
                        navHostController.popBackStack(ScreenRoute.FavoritesViewRoute.route, inclusive = false)
                    }
                )
            }
        }
    }
}