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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.CustomFont
import com.example.weatherapp.utils.ScreenRoute
import com.example.weatherapp.views.Home.ViewModel.HomeViewModel
import kotlinx.coroutines.delay

// دالة لتحويل الأرقام والرموز إلى النمط المحلي بناءً على اللغة (إذا لزم الأمر)
fun String.toLocalizedFormat(language: String): String {
    var result = this

    // تحويل الأرقام أولاً
    if (language == "ar") {
        val arabicDigits = "٠١٢٣٤٥٦٧٨٩"
        val westernDigits = "0123456789"
        for (i in westernDigits.indices) {
            result = result.replace(westernDigits[i], arabicDigits[i])
        }
    }

    return result
}

@Composable
fun SplashView(
    navController: NavController, // NavController كمعامل
    viewModel: HomeViewModel // إضافة HomeViewModel للوصول إلى اللغة
) {
    // الحصول على اللغة المختارة من ViewModel
    val language by viewModel.language.collectAsStateWithLifecycle()
    var isTextVisible by remember { mutableStateOf(false) }

    // تغيير اتجاه العرض بناءً على اللغة
    val layoutDirection = if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        // Automatically navigate to HomeView after 4 seconds
        LaunchedEffect(Unit) {
            delay(4000) // Delay for 4 seconds
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
                    text = if (language == "ar") "الطقس" else "Weather",
                    fontSize = 40.sp,
                    fontFamily = CustomFont,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
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
                        animationSpec = tween(durationMillis = 2000) // 2-second animation
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(durationMillis = 2000)
                    )
                ) {
                    Text(
                        text = if (language == "ar") "تحديثات الطقس الخاصة بك على الفور" else "Your Weather Update Instantly",
                        fontSize = 14.sp,
                        fontFamily = CustomFont,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}