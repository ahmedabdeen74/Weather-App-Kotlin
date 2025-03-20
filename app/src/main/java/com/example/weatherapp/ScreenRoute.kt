package com.example.weatherapp

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRoute(val route: String) {
    @Serializable
    object HomeViewRoute : ScreenRoute("home_view")
}
