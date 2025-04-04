# Weather Forecast Application 🌦️

An Android mobile application that provides real-time weather updates based on your location or a selected location. The app allows users to view weather details, set weather alerts, manage favorite locations, and customize settings like temperature units, wind speed units, and language.

## 📋 Project Overview

The **Weather Forecast Application** is designed to deliver accurate weather information using the [OpenWeatherMap API](https://openweathermap.org/api). It supports features like:

- Displaying current weather status and temperature for your location.
- Allowing users to pick a specific location via a map or search using auto-complete.
- Adding locations to a favorites list to quickly access their weather information.
- Setting weather alerts for conditions like rain, wind, extreme temperatures, fog, snow, etc.
- Customizing settings such as temperature units (Celsius, Kelvin, Fahrenheit), wind speed units (meter/sec, miles/hour), and language (Arabic, English).

### Demo Video
Watch the app in action: [Demo Video on Google Drive](https://drive.google.com/file/d/1-97vRFtzwuBhvp8Ufms00S3ofUBnM2K7/view?usp=sharing).

---

## 📱 App Screens

### 1. **Home Screen**
Displays the following weather details for the current or selected location:
- Current temperature, date, and time.
- Humidity, wind speed, pressure, and cloud coverage.
- City name, weather icon, and description (e.g., clear sky, light rain).
- Hourly forecast for the current day.
- 5-day weather forecast.

### 2. **Settings Screen**
Allows users to customize the app:
- **Location**: Choose between GPS or selecting a location from the map.
- **Units**:
  - Temperature: Kelvin, Celsius, Fahrenheit.
  - Wind Speed: meter/sec, miles/hour.
- **Language**: Arabic or English.

### 3. **Weather Alerts Screen**
- Add weather alerts with customizable settings:
  - Duration of the alert.
  - Alert type: Notification or Alarm sound.
  - Option to stop or turn off the alert.
- View and manage scheduled alerts.

### 4. **Favorite Screen**
- Lists favorite locations with the ability to:
  - View detailed weather forecasts for each location.
  - Add new locations using a map or auto-complete search.
  - Remove saved locations.
- Features a Floating Action Button (FAB) to add new favorite places.

---

## 🛠️ Technical Details

### Architecture
- **MVVM (Model-View-ViewModel)**: Ensures a clean separation of concerns and better maintainability.

### Tools & Technologies
- **Retrofit**: For making API calls to OpenWeatherMap.
- **Room**: For local database storage (e.g., favorite locations).
- **Coroutines**: For handling asynchronous tasks.
- **WorkManager**: For scheduling background tasks like weather alerts.
- **Jetpack Compose**: For building the UI.
- **Google Maps API**: For location selection and map integration.
- **Lottie Animations**: For engaging splash screen and empty state animations.

### API
- The app uses the [OpenWeatherMap Forecast API](https://openweathermap.org/api) to fetch weather data. Refer to the API documentation for details on endpoints and parameters.

### Unit Testing
- Unit tests are implemented to ensure the stability, quality, and accuracy of the app. Tests cover:
  - Local data source (Room database).
  - Repository layer.
  - ViewModel logic.
  - Mocking dependencies for isolated testing.

---

## 🚀 Features

- **Real-Time Weather Updates**: Get the latest weather information for your current or selected location.
- **Location Selection**: Use GPS or pick a location on the map with auto-complete search.
- **Favorite Locations**: Save and manage your favorite places for quick access.
- **Weather Alerts**: Set alerts for specific weather conditions with customizable notifications or alarms.
- **Multilingual Support**: Switch between Arabic and English.
- **Customizable Units**: Choose your preferred units for temperature and wind speed.
- **User-Friendly UI**: Built with Jetpack Compose for a modern and intuitive interface.

---

## 📦 Installation

1. **Clone the Repository**:
   ```bash
   https://github.com/ahmedabdeen74/Weather-App-Kotlin.git
