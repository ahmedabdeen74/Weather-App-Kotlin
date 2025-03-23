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
import com.example.weatherapp.repo.WeatherRepositoryImpl
import com.example.weatherapp.views.Home.View.HomeView

import com.google.android.gms.location.*
import android.location.Geocoder
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.views.Home.ViewModel.HomeViewModel
import com.example.weatherapp.views.Home.ViewModel.HomeViewModelFactory
import com.example.weatherapp.views.Settings.SettingsView
import java.util.Locale

class MainActivity : ComponentActivity() {

    lateinit var navHostController: NavHostController
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var homeViewModel: HomeViewModel

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

        // Initialize ViewModel
        val repository = WeatherRepositoryImpl.getInstance(RemoteDataSourceImpl())
        val factory = HomeViewModelFactory(repository)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

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
                    val geocoder = Geocoder(this, Locale.getDefault())
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
                    val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
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
                        navHostController.navigate(ScreenRoute.SettingViewRoute.route) {
                           // popUpTo(ScreenRoute.HomeViewRoute) { inclusive = true }
                        }
                    },
                    onHomeClick = {
                        navHostController.navigate(ScreenRoute.HomeViewRoute.route) {
                            navHostController.popBackStack()
                        }
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
        }
    }
}