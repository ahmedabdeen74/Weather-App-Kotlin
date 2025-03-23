package com.example.weatherapp.views.Settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation

import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable

import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.views.Home.ViewModel.HomeViewModel

enum class TemperatureUnit {
    CELSIUS,
    KELVIN,
    FAHRENHEIT
}
enum class WindSpeedUnit {
    METER_PER_SEC,
    MILE_PER_HOUR
}
@Composable
fun SettingsView(
    onBackClick: () -> Unit,
    viewModel: HomeViewModel
) {
    val windSpeedUnit by viewModel.windSpeedUnit.collectAsStateWithLifecycle()
    val windUnitIndex = when(windSpeedUnit) {
        WindSpeedUnit.METER_PER_SEC -> 0
        WindSpeedUnit.MILE_PER_HOUR -> 1
    }
    val temperatureUnit by viewModel.temperatureUnit.collectAsStateWithLifecycle()
    val tempUnitIndex = when(temperatureUnit) {
        TemperatureUnit.CELSIUS -> 0
        TemperatureUnit.KELVIN -> 1
        TemperatureUnit.FAHRENHEIT -> 2
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xff100b20),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
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
                    .size(160.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 160.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Language Setting
                SettingCategory(
                    title = "Language",
                    options = listOf("English", "Arabic"),
                    fontSize = 14.sp,
                    painter = painterResource(id = R.drawable.language),
                    selectedOption = 0  // English selected
                )

                // Location Setting
                SettingCategory(
                    title = "Location",
                    options = listOf("Gps", "Map"),
                    fontSize = 14.sp,
                    painter = painterResource(id = R.drawable.location),
                    selectedOption = 0  // Gps selected
                )

                // Temperature Unit Setting
                SettingCategory(
                    title = "Temp Unit",
                    options = listOf("Celsius °C", "Kelvin °K", "Fahrenheit °F"),
                    fontSize = 12.sp,
                    painter = painterResource(id = R.drawable.tempunit),
                    selectedOption = tempUnitIndex,
                    onOptionSelected = { index ->
                        val newUnit = when(index) {
                            0 -> TemperatureUnit.CELSIUS
                            1 -> TemperatureUnit.KELVIN
                            2 -> TemperatureUnit.FAHRENHEIT
                            else -> TemperatureUnit.CELSIUS
                        }
                        viewModel.setTemperatureUnit(newUnit)
                    }
                )

                // Wind Speed Unit Setting
                SettingCategory(
                    title = "Wind Speed Unit",
                    fontSize = 14.sp,
                    options = listOf("meter/sec", "mile/hour"),
                    painter = painterResource(id = R.drawable.windunit),
                    selectedOption = windUnitIndex,
                    onOptionSelected = { index ->
                        val newUnit = when(index) {
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
        // Background container image
        Image(
            painter = painterResource(id = R.drawable.container),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (options.size > 2) 130.dp else 110.dp),
            contentScale = ContentScale.FillBounds
        )

        // Content on top of the background
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

                // Category title
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }
             Spacer(
                 modifier = Modifier.height(8.dp)
             )
            // Options with radio buttons
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
                        // Add modifier with padding to increase distance between items
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .clickable { onOptionSelected(index) }

                    ) {
                        // Radio button (circle with white border)
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
                            // Filled white circle if selected
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

                        // Option text
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