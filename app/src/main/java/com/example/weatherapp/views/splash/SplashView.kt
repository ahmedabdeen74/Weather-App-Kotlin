package com.example.weatherapp.views.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.CustomFont
import com.example.weatherapp.utils.ScreenRoute
import kotlinx.coroutines.delay

@Composable
fun SplashView(
    navController: NavController // Add NavController as a parameter
) {
    var isTextVisible by remember { mutableStateOf(false) }

    // Automatically navigate to HomeView after 3 seconds
    LaunchedEffect(Unit) {
        delay(4000) // Delay for 3 seconds
        navController.navigate(ScreenRoute.HomeViewRoute.route) {
            popUpTo(ScreenRoute.SplashViewRoute.route) { inclusive = true } // Remove SplashView from back stack
        }
    }

    // Trigger the text animation when the composable is first composed
    LaunchedEffect(Unit) {
        isTextVisible = true
    }

    Scaffold(
        containerColor = Color(0xff100b20),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 180.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation4))
            val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

            Text(
                text = "Weather",
                fontSize = 40.sp,
                fontFamily = CustomFont,
                color = Color.White,
                fontWeight = FontWeight.Bold,

            )
            Spacer(modifier = Modifier.height(16.dp))
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = isTextVisible,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight }, // Start from the bottom
                    animationSpec = tween(durationMillis = 2000) // 1-second animation
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 2000)
                )
            ) {
                Text(
                    text = "Your Weather Update Instantly",
                    fontSize = 14.sp,
                    fontFamily = CustomFont,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

