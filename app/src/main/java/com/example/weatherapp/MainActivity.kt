package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.data.local.FavoriteLocationsDatabase
import com.example.weatherapp.data.local.FavoriteLocationsLocalDataSource
import com.example.weatherapp.data.local.WeatherAlertsDatabase
import com.example.weatherapp.data.local.WeatherAlertsLocalDataSourceImpl
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.entity.LocalDataSource
import com.example.weatherapp.data.local.entity.LocalDataSourceImpl
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.repo.FavoriteLocationsRepositoryImpl
import com.example.weatherapp.repo.WeatherAlertsRepositoryImpl
import com.example.weatherapp.repo.WeatherRepositoryImpl
import com.example.weatherapp.utils.AlarmReceiver
import com.example.weatherapp.utils.ScreenRoute
import com.example.weatherapp.views.Favourite.View.FavoritesView
import com.example.weatherapp.views.Favourite.View.MapSelectionView
import com.example.weatherapp.views.Favourite.ViewModel.FavoritesViewModel
import com.example.weatherapp.views.Favourite.ViewModel.FavoritesViewModelFactory
import com.example.weatherapp.views.Home.View.HomeView
import com.example.weatherapp.views.Home.ViewModel.HomeViewModel
import com.example.weatherapp.views.Home.ViewModel.HomeViewModelFactory
import com.example.weatherapp.views.Home.ViewModel.LocationSource
import com.example.weatherapp.views.Settings.SettingsView
import com.example.weatherapp.views.WeatherAlerts.view.WeatherAlertsView
import com.example.weatherapp.views.WeatherAlerts.viewModel.WeatherAlertsViewModel
import com.example.weatherapp.views.WeatherAlerts.viewModel.WeatherAlertsViewModelFactory
import com.example.weatherapp.views.splash.SplashView
import com.google.android.gms.location.*
import java.util.Locale

class MainActivity : ComponentActivity() {

    lateinit var navHostController: NavHostController
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var weatherAlertsViewModel: WeatherAlertsViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            getLastLocation()
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the status bar and navigation bar colors programmatically
        setSystemBarColors()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // HomeViewModel
        val weatherDatabase = WeatherDatabase.getInstance(this)
        val localDataSourceHome: LocalDataSource = LocalDataSourceImpl(weatherDatabase.weatherDao())
        val remoteDataSource = RemoteDataSourceImpl()
        val repository = WeatherRepositoryImpl.getInstance(remoteDataSource, localDataSourceHome)
        val factory = HomeViewModelFactory(repository, this)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]


        // FavoritesViewModel
        val favoriteLocationsDao = FavoriteLocationsDatabase.getInstance(this).favoriteLocationsDao()
        val localDataSource = FavoriteLocationsLocalDataSource(favoriteLocationsDao)
        val favoritesRepository = FavoriteLocationsRepositoryImpl.getInstance(localDataSource)
        val favoritesViewModelFactory = FavoritesViewModelFactory(favoritesRepository, this)
        favoritesViewModel = ViewModelProvider(this, favoritesViewModelFactory)[FavoritesViewModel::class.java]

        // WeatherAlertsViewModel
        val weatherAlertsDatabase = WeatherAlertsDatabase.getInstance(this)
        val weatherAlertsLocalDataSource = WeatherAlertsLocalDataSourceImpl(weatherAlertsDatabase.weatherAlertsDao())
        val weatherAlertsRepository = WeatherAlertsRepositoryImpl.getInstance(weatherAlertsLocalDataSource)
        val weatherAlertsViewModelFactory = WeatherAlertsViewModelFactory(this, weatherAlertsRepository)
        weatherAlertsViewModel = ViewModelProvider(this, weatherAlertsViewModelFactory)[WeatherAlertsViewModel::class.java]

        // Processing tapping on the sound notification to stop the sound
        val stopSound = intent.getBooleanExtra("stopSound", false)
        if (stopSound) {
            AlarmReceiver.stopAlarmSound()
        }

        requestLocationPermissions()

        setContent {
            navHostController = rememberNavController()
            SetupNavHost()
        }
    }

    private fun setSystemBarColors() {
        val window: Window = window
        val primaryColor = Color(108, 97, 181)

        // Ensure the system bars are drawn behind the content
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set the status bar color
        window.statusBarColor = primaryColor.toArgb()

        // Set the navigation bar color
        window.navigationBarColor = primaryColor.toArgb()

        // Set the status bar icons to light (white) for better visibility on the dark background
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = false

        // Set the navigation bar icons to light (white) for better visibility on the dark background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightNavigationBars = false
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
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )
        } else {
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
                if (location != null && homeViewModel.locationSource.value == LocationSource.GPS) {
                    val geocoder = android.location.Geocoder(this, Locale.getDefault())
                    homeViewModel.fetchWeatherData(location.latitude, location.longitude, geocoder)
                } else if (homeViewModel.locationSource.value == LocationSource.MAP) {
                } else {
                    requestLocationUpdates()
                }
            }
        }
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (homeViewModel.locationSource.value == LocationSource.GPS) {
                        val geocoder = android.location.Geocoder(this@MainActivity, Locale.getDefault())
                        homeViewModel.fetchWeatherData(location.latitude, location.longitude, geocoder)
                    }
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

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @Composable
    fun SetupNavHost() {
        NavHost(
            navController = navHostController,
            startDestination = ScreenRoute.SplashViewRoute.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(ScreenRoute.SplashViewRoute.route) {
                SplashView(navController = navHostController)
            }
            composable(ScreenRoute.HomeViewRoute.route) {
                HomeView(
                    viewModel = homeViewModel,
                    onSettingClick = {
                        navHostController.navigate(ScreenRoute.SettingViewRoute.route)
                    },
                    onHomeClick = {
                        navHostController.navigate(ScreenRoute.HomeViewRoute.route) {
                            popUpTo(ScreenRoute.HomeViewRoute.route) { inclusive = true }
                        }

                    },
                    onFavoriteClick = {
                        navHostController.navigate(ScreenRoute.FavoritesViewRoute.route)
                    },
                    onAlertsClick = {
                        navHostController.navigate(ScreenRoute.WeatherAlertsViewRoute.route)
                    }
                )
            }
            composable(ScreenRoute.SettingViewRoute.route) {
                SettingsView(
                    viewModel = homeViewModel,
                    onBackClick = {
                        navHostController.popBackStack(ScreenRoute.HomeViewRoute.route, inclusive = false)
                    },
                    navController = navHostController,
                    fusedLocationClient = fusedLocationClient
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
                    },
                    navController = navHostController,
                    homeViewModel = homeViewModel
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
            composable(ScreenRoute.WeatherAlertsViewRoute.route) {
                WeatherAlertsView(
                    viewModel = weatherAlertsViewModel,
                    onBackClick = {
                        navHostController.popBackStack(ScreenRoute.HomeViewRoute.route, inclusive = false)
                    }
                )
            }
        }
    }
}