package com.example.weatherapp.views.Home.View

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.ui.theme.CustomFont
import com.example.weatherapp.views.Home.ViewModel.ForecastDisplay
import com.example.weatherapp.views.Home.ViewModel.ForecastState
import com.example.weatherapp.views.Home.ViewModel.HomeViewModel
import com.example.weatherapp.views.Home.ViewModel.WeatherState
import com.example.weatherapp.views.Settings.TemperatureUnit
import com.example.weatherapp.views.Settings.WindSpeedUnit
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

enum class SheetState {
    COLLAPSED, EXPANDED
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
        result = result.replace(" m", " م")
        result = result.replace("mb", "ملي بار")
        result = result.replace("H:", "ع :")
        result = result.replace("L:", "ص :")
    }

    return result
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    viewModel: HomeViewModel,
    onHomeClick: () -> Unit,
    onSettingClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAlertsClick: () -> Unit,
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val layoutDirection = if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
        val forecastState by viewModel.forecastState.collectAsStateWithLifecycle()
        val locationName by viewModel.locationName.collectAsStateWithLifecycle()
        val temperatureUnit by viewModel.temperatureUnit.collectAsStateWithLifecycle()
        val windSpeedUnit by viewModel.windSpeedUnit.collectAsStateWithLifecycle()
        val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
        val lastUpdated by viewModel.lastUpdated.collectAsStateWithLifecycle()

        val bottomAppBarHeight = 100.dp
        var sheetState by remember { mutableStateOf(SheetState.COLLAPSED) }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenHeight = maxHeight

            Scaffold(
                bottomBar = {
                    BottomAppBar(
                        modifier = Modifier
                            .height(bottomAppBarHeight)
                            .clip(RoundedCornerShape(24.dp)),
                        containerColor = Color(46, 13, 99).copy(alpha = 0.7f)
                    ) {
                        IconButton(
                            onClick = { onHomeClick() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(id = R.drawable.home),
                                contentDescription = if (language == "ar") "الرئيسية" else "Home",
                                tint = Color.White
                            )
                        }
                        IconButton(
                            onClick = { onFavoriteClick() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(id = R.drawable.map),
                                contentDescription = if (language == "ar") "المفضلة" else "Favorite",
                                tint = Color.White
                            )
                        }
                        IconButton(
                            onClick = { onAlertsClick() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(id = R.drawable.alarm),
                                contentDescription = if (language == "ar") "التنبيهات" else "Alarm",
                                tint = Color.White
                            )
                        }
                        IconButton(
                            onClick = { onSettingClick() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(id = R.drawable.settings),
                                contentDescription = if (language == "ar") "الإعدادات" else "Settings",
                                tint = Color.White
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1B0D67).copy(alpha = 0.7f))
                        .padding(paddingValues)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.weatherbackground),
                        contentDescription = "Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (!isOnline) {
                        OfflineBanner(lastUpdated = lastUpdated, language = language)
                    }

                    when (weatherState) {
                        is WeatherState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFF00BFFF))
                            }
                        }
                        is WeatherState.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (language == "ar") "خطأ: ${(weatherState as WeatherState.Error).message}" else "Error: ${(weatherState as WeatherState.Error).message}",
                                    color = Color.White
                                )
                            }
                        }
                        is WeatherState.Success -> {
                            val weatherData = (weatherState as WeatherState.Success).data

                            val tempSymbol = when (temperatureUnit) {
                                TemperatureUnit.CELSIUS -> "°C"
                                TemperatureUnit.KELVIN -> "°K"
                                TemperatureUnit.FAHRENHEIT -> "°F"
                            }

                            val mainTemp = viewModel.convertTemperature(weatherData.main.temp)
                            val minTemp = viewModel.convertTemperature(weatherData.main.temp_min)
                            val maxTemp = viewModel.convertTemperature(weatherData.main.temp_max)

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 65.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = locationName,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = CustomFont,
                                    color = Color.White
                                )

                                Row(
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(
                                        text = "${mainTemp.roundToInt()}".toLocalizedFormat(language),
                                        fontSize = 96.sp,
                                        fontWeight = FontWeight.Thin,
                                        color = Color.White
                                    )
                                    Text(
                                        text = tempSymbol.toLocalizedFormat(language),
                                        fontSize = 44.sp,
                                        fontWeight = FontWeight.Thin,
                                        color = Color.White,
                                        modifier = Modifier.padding(bottom = 48.dp)
                                    )
                                }

                                Text(
                                    text = weatherData.weather.firstOrNull()?.description?.capitalize() ?: if (language == "ar") "غير معروف" else "Unknown",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = CustomFont,
                                    color = Color(0xFFA9A9A9)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row {
                                    Text(
                                        text = "H:${maxTemp.roundToInt()}".toLocalizedFormat(language),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = CustomFont,
                                        color = Color.White
                                    )
                                    Text(
                                        text = tempSymbol.toLocalizedFormat(language),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White,
                                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                    )
                                    Text(
                                        text = "  L:${minTemp.roundToInt()}".toLocalizedFormat(language),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = CustomFont,
                                        color = Color.White
                                    )
                                    Text(
                                        text = tempSymbol.toLocalizedFormat(language),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White,
                                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier.height(24.dp)
                            )

                            Image(
                                painter = painterResource(id = R.drawable.house),
                                contentDescription = "Overlay",
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .size(350.dp)
                            )

                            WeatherBottomSheet(
                                sheetState = sheetState,
                                onSheetStateChange = { newState -> sheetState = newState },
                                weatherData = weatherData,
                                forecastState = forecastState,
                                viewModel = viewModel,
                                temperatureUnit = temperatureUnit,
                                windSpeedUnit = windSpeedUnit,
                                language = language,
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherBottomSheet(
    sheetState: SheetState,
    onSheetStateChange: (SheetState) -> Unit,
    weatherData: WeatherResponse,
    forecastState: ForecastState,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    temperatureUnit: TemperatureUnit,
    windSpeedUnit: WindSpeedUnit,
    language: String
) {
    val windSpeedUnitState by viewModel.windSpeedUnit.collectAsStateWithLifecycle()
    val windSpeedValue = viewModel.convertWindSpeed(weatherData.wind.speed)
    val windSpeedUnitText = when (windSpeedUnitState) {
        WindSpeedUnit.METER_PER_SEC -> "m/s"
        WindSpeedUnit.MILE_PER_HOUR -> "mph"
    }

    val tempSymbol = when (temperatureUnit) {
        TemperatureUnit.CELSIUS -> "°C"
        TemperatureUnit.KELVIN -> "°K"
        TemperatureUnit.FAHRENHEIT -> "°F"
    }
    var selectedHourlyIndex by remember { mutableStateOf(0) }
    val topPartHeight = when (sheetState) {
        SheetState.COLLAPSED -> 280.dp
        SheetState.EXPANDED -> 500.dp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(topPartHeight)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color(46, 13, 99).copy(alpha = 0.7f))
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    if (dragAmount < 0 && sheetState != SheetState.EXPANDED) {
                        onSheetStateChange(SheetState.EXPANDED)
                    } else if (dragAmount > 0 && sheetState != SheetState.COLLAPSED) {
                        onSheetStateChange(SheetState.COLLAPSED)
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
        if (sheetState == SheetState.COLLAPSED) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (language == "ar") "توقعات كل ساعة" else "Hourly Forecast",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CustomFont,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Start
                )
                when (forecastState) {
                    is ForecastState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF00BFFF))
                        }
                    }
                    is ForecastState.Error -> {
                        Text(
                            text = if (language == "ar") "تعذر تحميل التوقعات" else "Couldn't load forecast",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    is ForecastState.Success -> {
                        val forecastItems = forecastState.data

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            itemsIndexed(forecastItems.take(8)) { index, item ->
                                ForecastItem(
                                    forecastDisplay = item,
                                    isSelected = index == selectedHourlyIndex,
                                    onClick = { selectedHourlyIndex = index },
                                    viewModel = viewModel,
                                    temperatureUnit = temperatureUnit,
                                    language = language
                                )
                            }
                        }
                    }
                }
            }
        } else if (sheetState == SheetState.EXPANDED) {
            IconButton(
                onClick = { onSheetStateChange(SheetState.COLLAPSED) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.cancel),
                    contentDescription = if (language == "ar") "إغلاق" else "Close",
                    tint = Color.White
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = if (language == "ar") "الجدول الزمني للطقس" else "Weather Timeline",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CustomFont,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Start
                    )

                    when (forecastState) {
                        is ForecastState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(180.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFF00BFFF))
                            }
                        }
                        is ForecastState.Error -> {
                            Text(
                                text = if (language == "ar") "تعذر تحميل التوقعات" else "Couldn't load forecast",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        is ForecastState.Success -> {
                            val forecastItems = forecastState.data
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                itemsIndexed(forecastItems.take(40)) { index, item ->
                                    ForecastItem(
                                        forecastDisplay = item,
                                        isSelected = index == selectedHourlyIndex,
                                        onClick = { selectedHourlyIndex = index },
                                        viewModel = viewModel,
                                        temperatureUnit = temperatureUnit,
                                        language = language
                                    )
                                }
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        WeatherCard(
                            label = if (language == "ar") "الرياح" else "WIND",
                            value = "${String.format("%.1f", windSpeedValue)} $windSpeedUnitText".toLocalizedFormat(language),
                            iconResId = R.drawable.wind
                        )
                        WeatherCard(
                            label = if (language == "ar") "الإحساس" else "FEELS LIKE",
                            value = "${viewModel.convertTemperature(weatherData.main.feels_like).roundToInt()}$tempSymbol".toLocalizedFormat(language),
                            iconResId = R.drawable.feelslike
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        WeatherCard(
                            label = if (language == "ar") "الرؤية" else "VISIBILITY",
                            value = "${weatherData.visibility} m".toLocalizedFormat(language),
                            iconResId = R.drawable.visibility
                        )
                        WeatherCard(
                            label = if (language == "ar") "الغيوم" else "CLOUDS",
                            value = "${weatherData.clouds.all}%".toLocalizedFormat(language),
                            iconResId = R.drawable.clouds
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        WeatherCard(
                            label = if (language == "ar") "الرطوبة" else "HUMIDITY",
                            value = "${weatherData.main.humidity}%".toLocalizedFormat(language),
                            iconResId = R.drawable.humidity
                        )
                        WeatherCard(
                            label = if (language == "ar") "الضغط" else "PRESSURE",
                            value = "${weatherData.main.pressure}mb".toLocalizedFormat(language),
                            iconResId = R.drawable.pressure
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherCard(
    label: String,
    value: String,
    iconResId: Int
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp)
            .padding(8.dp)
            .border(
                width = 2.dp,
                color = Color(108, 97, 181),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(46, 13, 99)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = label,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFA9A9A9)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun ForecastItem(
    forecastDisplay: ForecastDisplay,
    isSelected: Boolean,
    onClick: () -> Unit,
    viewModel: HomeViewModel,
    temperatureUnit: TemperatureUnit,
    language: String
) {
    val convertedTemp = viewModel.convertTemperature(forecastDisplay.temp.toDouble()).roundToInt()

    val tempSymbol = when (temperatureUnit) {
        TemperatureUnit.CELSIUS -> "°C"
        TemperatureUnit.KELVIN -> "°K"
        TemperatureUnit.FAHRENHEIT -> "°F"
    }

    val currentCalendar = Calendar.getInstance()
    // Use the same Locale used in HomeViewModel
    val locale = when (language) {
        "ar" -> Locale("ar")
        else -> Locale("en")
    }
    val dayFormat = SimpleDateFormat("EEEE", locale)
    val currentDay = dayFormat.format(currentCalendar.time)

    val displayDay = if (forecastDisplay.day == currentDay) {
        if (language == "ar") "اليوم" else "Today"
    } else {
        forecastDisplay.day
    }

    Box(
        modifier = Modifier
            .width(120.dp)
            .height(190.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 2.dp,
                color = Color(108, 97, 181),
                shape = RoundedCornerShape(24.dp)
            )
            .background(
                if (isSelected) Color(108, 97, 181) else Color(46, 13, 99)
            )
            .padding(8.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val dayTextColor = if (displayDay == "Today" || displayDay == "اليوم") Color(0xFFFFD700) else Color.White

            Text(
                text = displayDay,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = dayTextColor,
                fontFamily = CustomFont
            )
            Text(
                text = forecastDisplay.time.toLocalizedFormat(language),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF00BFFF),
                fontFamily = CustomFont
            )
            val weatherIcon = getWeatherIconResource(forecastDisplay.description, language)
            Image(
                painter = painterResource(id = weatherIcon),
                contentDescription = forecastDisplay.description,
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit
            )

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "$convertedTemp".toLocalizedFormat(language),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = CustomFont
                )
                Text(
                    text = tempSymbol.toLocalizedFormat(language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                    fontFamily = CustomFont
                )
            }

            Text(
                text = forecastDisplay.description.capitalize(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFA9A9A9),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = CustomFont
            )
        }
    }
}

fun String.capitalize(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
            else it.toString()
        }
    }
}

@Composable
fun OfflineBanner(lastUpdated: Long?, language: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(46, 13, 99).copy(alpha = 0.8f))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.wifi_off),
                    contentDescription = if (language == "ar") "لا يوجد إنترنت" else "No Internet",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == "ar") "لا يوجد اتصال بالإنترنت" else "No Internet Connection",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (lastUpdated != null && lastUpdated > 0) {
                val dateFormat = SimpleDateFormat("hh:mm a", if (language == "ar") Locale("ar") else Locale.getDefault())
                val formattedTime = dateFormat.format(lastUpdated)
                Text(
                    text = (if (language == "ar") "آخر تحديث: " else "Last updated: ") + formattedTime.toLocalizedFormat(language),
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 24.dp)
                )
            }
        }
    }
}