package com.example.weatherapp.views.Settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.*
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.CustomFont
import com.example.weatherapp.utils.ScreenRoute
import com.example.weatherapp.views.Home.ViewModel.HomeViewModel
import com.example.weatherapp.views.Home.ViewModel.LocationSource
import com.google.android.gms.location.FusedLocationProviderClient
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.TextUnit
import androidx.navigation.NavHostController

enum class TemperatureUnit {
    CELSIUS, KELVIN, FAHRENHEIT
}

enum class WindSpeedUnit {
    METER_PER_SEC, MILE_PER_HOUR
}

@Composable
fun SettingsView(
    onBackClick: () -> Unit,
    viewModel: HomeViewModel,
    navController: NavHostController,
    fusedLocationClient: FusedLocationProviderClient
) {
    val windSpeedUnit by viewModel.windSpeedUnit.collectAsStateWithLifecycle()
    val windUnitIndex = when (windSpeedUnit) {
        WindSpeedUnit.METER_PER_SEC -> 0
        WindSpeedUnit.MILE_PER_HOUR -> 1
    }
    val temperatureUnit by viewModel.temperatureUnit.collectAsStateWithLifecycle()
    val tempUnitIndex = when (temperatureUnit) {
        TemperatureUnit.CELSIUS -> 0
        TemperatureUnit.KELVIN -> 1
        TemperatureUnit.FAHRENHEIT -> 2
    }
    val locationSource by viewModel.locationSource.collectAsStateWithLifecycle()
    val locationOption = when (locationSource) {
        LocationSource.GPS -> 0
        LocationSource.MAP -> 1
    }
    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasLocationPermission = isGranted }

    // Update wind speed unit selection based on temperature unit change
    LaunchedEffect(temperatureUnit) {
        when (temperatureUnit) {
            TemperatureUnit.CELSIUS, TemperatureUnit.KELVIN -> viewModel.setWindSpeedUnit(WindSpeedUnit.METER_PER_SEC)
            TemperatureUnit.FAHRENHEIT -> viewModel.setWindSpeedUnit(WindSpeedUnit.MILE_PER_HOUR)
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xff100b20),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(vertical = 32.dp, horizontal = 16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(108, 97, 181),
                    modifier = Modifier.size(35.dp)
                )
            }

            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.animation1)
            )
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 160.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SettingCategory(
                    title = "Language",
                    options = listOf("Default", "English", "Arabic"),
                    fontSize = 14.sp,
                    painter = painterResource(id = R.drawable.language),
                    selectedOption = 0
                )

                SettingCategory(
                    title = "Location",
                    options = listOf("GPS", "Map"),
                    fontSize = 14.sp,
                    painter = painterResource(id = R.drawable.location),
                    selectedOption = locationOption,
                    onOptionSelected = { index ->
                        when (index) {
                            0 -> { // GPS
                                viewModel.setLocationSource(LocationSource.GPS)
                                if (hasLocationPermission) {
                                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                        if (location != null) {
                                            val geocoder = android.location.Geocoder(context)
                                            viewModel.fetchWeatherData(location.latitude, location.longitude, geocoder)
                                        }
                                    }
                                }
                            }
                            1 -> { // Map
                                viewModel.setLocationSource(LocationSource.MAP)
                                // navController.navigate(ScreenRoute.FavoritesViewRoute.route)
                            }
                        }
                    }
                )

                SettingCategory(
                    title = "Temp Unit",
                    options = listOf("Celsius °C", "Kelvin °K", "Fahrenheit °F"),
                    fontSize = 12.sp,
                    painter = painterResource(id = R.drawable.tempunit),
                    selectedOption = tempUnitIndex,
                    onOptionSelected = { index ->
                        val newUnit = when (index) {
                            0 -> TemperatureUnit.CELSIUS
                            1 -> TemperatureUnit.KELVIN
                            2 -> TemperatureUnit.FAHRENHEIT
                            else -> TemperatureUnit.CELSIUS
                        }
                        viewModel.setTemperatureUnit(newUnit)
                    }
                )

                SettingCategory(
                    title = "Wind Speed Unit",
                    options = listOf("meter/sec", "mile/hour"),
                    fontSize = 14.sp,
                    painter = painterResource(id = R.drawable.windunit),
                    selectedOption = windUnitIndex,
                    onOptionSelected = { index ->
                        val newUnit = when (index) {
                            0 -> WindSpeedUnit.METER_PER_SEC
                            1 -> WindSpeedUnit.MILE_PER_HOUR
                            else -> WindSpeedUnit.METER_PER_SEC
                        }
                        viewModel.setWindSpeedUnit(newUnit)
                    }
                )
            }
        }
    }
}

@Composable
fun SettingCategory(
    title: String,
    options: List<String>,
    painter: Painter,
    fontSize: TextUnit,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.container),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (options.size > 2) 130.dp else 110.dp),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 32.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (options.size == 2)
                    Arrangement.SpaceEvenly
                else
                    Arrangement.SpaceBetween
            ) {
                options.forEachIndexed { index, option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .clickable { onOptionSelected(index) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .padding(4.dp)
                        ) {
                            if (index == selectedOption) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            color = Color(0xFF00BFFF),
                                            shape = CircleShape
                                        )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = option,
                            fontSize = fontSize,
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}