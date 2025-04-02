package com.example.weatherapp.views.Home.View

import com.example.weatherapp.R

fun getWeatherIconResource(description: String, language: String): Int {
    val desc = description.lowercase()
    return when {
        (language == "ar" && desc.contains("سماء صافية")) || (language != "ar" && desc.contains("clear")) -> R.drawable.clear
        (language == "ar" && desc.contains("غائم جزئي")) || (language != "ar" && desc.contains("few clouds")) -> R.drawable.fewcloud
        (language == "ar" && desc.contains("غيوم متفرقة")) || (language != "ar" && desc.contains("scattered clouds")) -> R.drawable.scatteredclouds
        (language == "ar" && desc.contains("غيوم متناثرة")) || (language != "ar" && desc.contains("scattered clouds")) -> R.drawable.scatteredclouds
        (language == "ar" && (desc.contains("غيوم قاتمة") || desc.contains("غطاء كامل"))) || (language != "ar" && (desc.contains("broken clouds") || desc.contains("overcast"))) -> R.drawable.brokenclouds
        (language == "ar" && (desc.contains("أمطار خفيفة") || desc.contains("رذاذ"))) || (language != "ar" && (desc.contains("shower rain") || desc.contains("drizzle"))) -> R.drawable.showerrain
        (language == "ar" && desc.contains("مطر")) || (language != "ar" && desc.contains("rain")) -> R.drawable.rain
        (language == "ar" && desc.contains("عاصفة رعدية")) || (language != "ar" && desc.contains("thunderstorm")) -> R.drawable.thunderstorm
        (language == "ar" && desc.contains("ثلج")) || (language != "ar" && desc.contains("snow")) -> R.drawable.snow
        (language == "ar" && (desc.contains("ضباب") || desc.contains("ضباب خفيف") || desc.contains("دخان"))) || (language != "ar" && (desc.contains("mist") || desc.contains("fog") || desc.contains("haze") || desc.contains("smoke"))) -> R.drawable.smokeweather
        else -> R.drawable.error
    }
}