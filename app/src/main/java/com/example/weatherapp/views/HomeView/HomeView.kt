package com.example.weatherapp.views.HomeView

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
import com.example.weatherapp.ui.theme.CustomFont


enum class SheetState {
    COLLAPSED, EXPANDED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(onHomeClick: () -> Unit) {

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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 65.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Montreal",            // lon & lat transform to text description
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CustomFont,
                        color = Color.White
                    )
                    Text(
                        text = "19°",        // temp
                        fontSize = 96.sp,
                        fontWeight = FontWeight.Thin,
                        color = Color.White
                    )
                    Text(
                        text = "Mostly Clear",        // weather description
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = CustomFont,
                        color = Color.Gray
                    )
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                    Text(
                        text = "H:24°  L:18°",       // temp_max and temp_min
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

                PermanentBottomSheetContent(
                    sheetState = sheetState,
                    onSheetStateChange = { newState ->
                        sheetState = newState
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
fun PermanentBottomSheetContent(
    sheetState: SheetState,
    onSheetStateChange: (SheetState) -> Unit,
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
                    WeatherCard("WIND", "4.7 m/s", R.drawable.wind)                 // wind speed
                    WeatherCard("FEELS LIKE", "25°", R.drawable.feelslike)         // feels_like
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherCard("Visibility", "1000 m", R.drawable.visibility)    // visibility
                    WeatherCard("Clouds", "0", R.drawable.clouds)                // clouds
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherCard("Humidity", "42%", R.drawable.humidity)        // humidity
                    WeatherCard("PRESSURE", "1011 mb", R.drawable.pressure)   // pressure
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

