package com.example.weatherapp.views.Home.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.ui.theme.CustomFont
import com.example.weatherapp.views.Home.ViewModel.HomeViewModel
import com.example.weatherapp.views.Home.ViewModel.WeatherState
import kotlin.math.roundToInt

enum class SheetState {
    COLLAPSED, EXPANDED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    viewModel: HomeViewModel,
    onHomeClick: () -> Unit
) {
    val weatherState = viewModel.weatherState
    val locationName = viewModel.locationName

    val bottomAppBarHeight = 60.dp
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
                            contentDescription = "Home",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { /* TODO: Action 2 */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(id = R.drawable.favorite),
                            contentDescription = "Favorite",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { /* TODO: Action 3 */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(id = R.drawable.map),
                            contentDescription = "Map",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { /* TODO: Action 4 */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(id = R.drawable.alarm),
                            contentDescription = "Alarm",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { /* TODO: Action 5 */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(id = R.drawable.settings),
                            contentDescription = "Settings",
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

                when (weatherState) {
                    is WeatherState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    is WeatherState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error: ${(weatherState as WeatherState.Error).message}",
                                color = Color.White
                            )
                        }
                    }
                    is WeatherState.Success -> {
                        val weatherData = (weatherState as WeatherState.Success).data

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
                            Text(
                                text = "${weatherData.main.temp.roundToInt()}°",
                                fontSize = 96.sp,
                                fontWeight = FontWeight.Thin,
                                color = Color.White
                            )
                            Text(
                                text = weatherData.weather.firstOrNull()?.description?.capitalize() ?: "Unknown",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = CustomFont,
                                color = Color.Gray
                            )
                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )
                            Text(
                                text = "H:${weatherData.main.temp_max.roundToInt()}°  L:${weatherData.main.temp_min.roundToInt()}°",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = CustomFont,
                                color = Color.White
                            )
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
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
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
    modifier: Modifier = Modifier
) {
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

        if (sheetState == SheetState.EXPANDED) {
            IconButton(
                onClick = { onSheetStateChange(SheetState.COLLAPSED) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.cancel),
                    contentDescription = "Close",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherCard("WIND", "${weatherData.wind.speed} m/s", R.drawable.wind)
                    WeatherCard("FEELS LIKE", "${weatherData.main.feels_like.roundToInt()}°", R.drawable.feelslike)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherCard("VISIBILITY", "${weatherData.visibility} m", R.drawable.visibility)
                    WeatherCard("CLOUDS", "${weatherData.clouds.all}%", R.drawable.clouds)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherCard("HUMIDITY", "${weatherData.main.humidity}%", R.drawable.humidity)
                    WeatherCard("PRESSURE", "${weatherData.main.pressure} mb", R.drawable.pressure)
                }
            }
        }
    }
}

@Composable
fun WeatherCard(label: String, value: String, iconResId: Int) {
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
                    color = Color.White
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

// Extension function to capitalize first letter of each word
fun String.capitalize(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault())
            else it.toString()
        }
    }
}