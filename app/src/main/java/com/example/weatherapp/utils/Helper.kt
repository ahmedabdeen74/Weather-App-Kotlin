import com.example.weatherapp.R

fun getWeatherIconResource(description: String): Int {
    return when {
        description.contains("clear") -> R.drawable.clear
        description.contains("few clouds") -> R.drawable.fewcloud
        description.contains("scattered clouds") -> R.drawable.scatteredclouds
        description.contains("broken clouds") || description.contains("overcast") -> R.drawable.brokenclouds
        description.contains("shower rain") || description.contains("drizzle") -> R.drawable.showerrain
        description.contains("rain") -> R.drawable.rain
        description.contains("thunderstorm") -> R.drawable.thunderstorm
        description.contains("snow") -> R.drawable.snow
        description.contains("mist") || description.contains("fog") ||
                description.contains("haze") || description.contains("smoke") -> R.drawable.smokeweather
        else -> R.drawable.error
    }
}