package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.views.HomeView.HomeView

class MainActivity : ComponentActivity() {
    lateinit var navHostController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            navHostController = rememberNavController()
            SetupNavHost()
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
                    onHomeClick = {
                        navHostController.navigate(ScreenRoute.HomeViewRoute.route) {
                            navHostController.popBackStack() // log out from app
                        }
                    }
                )
            }
        }
    }}




