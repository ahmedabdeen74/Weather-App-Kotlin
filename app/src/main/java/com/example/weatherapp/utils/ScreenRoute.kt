package com.example.weatherapp.utils

sealed class ScreenRoute(val route: String) {
    object SplashViewRoute : ScreenRoute("splash_view")
    object HomeViewRoute : ScreenRoute("home_view")
    object SettingViewRoute : ScreenRoute("setting_view")
    object FavoritesViewRoute : ScreenRoute("favorites_view")
    object MapSelectionViewRoute : ScreenRoute("map_view")
    object WeatherAlertsViewRoute : ScreenRoute("weatherAlerts_view")
}
