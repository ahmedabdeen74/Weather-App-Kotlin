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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.CustomFont
import com.example.weatherapp.utils.ScreenRoute
import com.example.weatherapp.views.Home.ViewModel.HomeViewModel
import com.example.weatherapp.views.Home.ViewModel.LocationSource
import com.google.android.gms.location.FusedLocationProviderClient
import java.util.Locale
enum class TemperatureUnit {
    CELSIUS, KELVIN, FAHRENHEIT
}

enum class WindSpeedUnit {
    METER_PER_SEC, MILE_PER_HOUR
}

fun String.toLocalizedFormat(language: String): String {
    var result = this
    if (language == "ar") {
        val arabicDigits = "٠١٢٣٤٥٦٧٨٩"
        val westernDigits = "0123456789"
        for (i in westernDigits.indices) {
            result = result.replace(westernDigits[i], arabicDigits[i])
        }
    }
    if (language == "ar") {
        result = result.replace("%", "٪")
        result = result.replace("°C", "°س")
        result = result.replace("°K", "°ك")
        result = result.replace("°F", "°ف")
        result = result.replace("m/s", "م/ث")
        result = result.replace("mph", "ميل/س")
        result = result.replace("meter/sec", "م/ث")
        result = result.replace("mile/hour", "ميل/س")
    }
    return result
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
    val language by viewModel.language.collectAsStateWithLifecycle()
    val isLanguageDefault by viewModel.isLanguageDefault.collectAsStateWithLifecycle()

    val languageIndex = if (isLanguageDefault) {
        0
    } else {
        when (language) {
            "en" -> 1 // English
            "ar" -> 2 // Arabic
            else -> 1
        }
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

    val layoutDirection = if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
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
                    if (language == "ar") {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "رجوع",
                            tint = Color(108, 97, 181),
                            modifier = Modifier.size(35.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(108, 97, 181),
                            modifier = Modifier.size(35.dp)
                        )
                    }
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
                        .padding(top = 150.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    SettingCategory(
                        title = if (language == "ar") "اللغة" else "Language",
                        options = if (language == "ar") listOf("لغة الجهاز", "الإنجليزية", "العربية") else listOf("Default", "English", "Arabic"),
                        fontSize = 14.sp,
                        painter = painterResource(id = R.drawable.language),
                        selectedOption = languageIndex,
                        onOptionSelected = { index ->
                            when (index) {
                                0 -> { // Default
                                    viewModel.setLanguageBasedOnDevice()
                                }
                                1 -> { // English
                                    viewModel.setLanguage("en")
                                }
                                2 -> { // Arabic
                                    viewModel.setLanguage("ar")
                                }
                            }
                        },
                        language = language
                    )

                    SettingCategory(
                        title = if (language == "ar") "الموقع" else "Location",
                        options = if (language == "ar") listOf("نظام تحديد المواقع", "الخريطة") else listOf("GPS", "Map"),
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
                        },
                        language = language
                    )

                    SettingCategory(
                        title = if (language == "ar") "وحدة درجة الحرارة" else "Temp Unit",
                        options = if (language == "ar") listOf("سلسيوس °C", "كلفن °K", "فهرنهايت °F") else listOf("Celsius °C", "Kelvin °K", "Fahrenheit °F"),
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
                        },
                        language = language
                    )

                    SettingCategory(
                        title = if (language == "ar") "وحدة سرعة الرياح" else "Wind Speed Unit",
                        options = if (language == "ar") listOf("متر/ثانية", "ميل/ساعة") else listOf("meter/sec", "mile/hour"),
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
                        },
                        language = language
                    )
                }
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
    onOptionSelected: (Int) -> Unit = {},
    language: String
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
                .height(if (options.size > 2) 135.dp else 115.dp),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 32.dp)
        ) {
            if (language == "ar") {
                Spacer(modifier = Modifier.height(6.dp))
            }

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
                            text = option.toLocalizedFormat(language),
                            fontSize = fontSize,
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}