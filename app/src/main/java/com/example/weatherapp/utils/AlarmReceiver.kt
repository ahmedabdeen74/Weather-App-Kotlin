package com.example.weatherapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import java.text.SimpleDateFormat
import java.util.Locale

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private var currentRingtone: Ringtone? = null
        const val ACTION_STOP_ALARM = "com.example.weatherapp.STOP_ALARM"

        fun stopAlarmSound() {
            currentRingtone?.stop()
            currentRingtone = null
            Log.d("AlarmDebug", "Alarm sound stopped successfully")
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_STOP_ALARM) {
            stopAlarmSound()
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(intent.getStringExtra("alertId")?.hashCode() ?: 0)
            Log.d("AlarmDebug", "Received ACTION_STOP_ALARM, notification canceled")
            return
        }

        Log.d("AlarmDebug", "Alarm received with ID: ${intent.getStringExtra("alertId")}")
        val alertType = intent.getStringExtra("alertType")
        val alertId = intent.getStringExtra("alertId") ?: run {
            Log.e("AlarmDebug", "No alertId provided in intent")
            return
        }
        val triggerTime = intent.getLongExtra("triggerTime", 0)

        // Retrieve the language, weather description, and location name from SharedPreferences
        val sharedPreferences = context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("language", "en") ?: "en" // Default English
        val weatherDescription = sharedPreferences.getString("weather_description", "Unknown") ?: "Unknown"
        val locationName = sharedPreferences.getString("location_name", "Unknown") ?: "Unknown"

        // Log trigger time with the correct Locale
        val locale = getLocaleForLanguage(language)
        Log.d("AlarmDebug", "Trigger time: ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale).format(triggerTime)}")

        createNotificationChannel(context, language)
        when (alertType) {
            "NOTIFICATION" -> showNotification(context, intent, weatherDescription, locationName, language)
            "ALARM" -> showNotificationWithSound(context, intent, weatherDescription, locationName, language)
            else -> Log.e("AlarmDebug", "Unknown alert type: $alertType")
        }
    }

    private fun showNotification(
        context: Context,
        intent: Intent,
        weatherDescription: String,
        locationName: String,
        language: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alertId = intent.getStringExtra("alertId") ?: return
        val triggerTime = intent.getLongExtra("triggerTime", 0)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Time format based on language
        val locale = getLocaleForLanguage(language)
        val timeFormat = SimpleDateFormat("HH:mm", locale)
        val formattedTime = timeFormat.format(triggerTime)

        val notification = NotificationCompat.Builder(context, "weather_channel")
            .setSmallIcon(R.drawable.application_icon)
            .setContentTitle(if (language == "ar") "تحديث الطقس" else "Weather Update")
            .setContentText(
                if (language == "ar")
                    "الطقس $weatherDescription في $locationName الساعة $formattedTime"
                else
                    "It's $weatherDescription in $locationName at $formattedTime"
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openPendingIntent)
            .setAutoCancel(true)
            .build()

        Log.d("AlarmDebug", "Showing notification for ID: $alertId at ${System.currentTimeMillis()}")
        notificationManager.notify(alertId.hashCode(), notification)
        Log.d("AlarmDebug", "Notification successfully shown for ID: $alertId")
    }

    private fun showNotificationWithSound(
        context: Context,
        intent: Intent,
        weatherDescription: String,
        locationName: String,
        language: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alertId = intent.getStringExtra("alertId") ?: return
        val triggerTime = intent.getLongExtra("triggerTime", 0)

        // Play audio
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        currentRingtone = RingtoneManager.getRingtone(context, alarmSound)
        if (currentRingtone != null) {
            Log.d("AlarmDebug", "Starting alarm sound")
            currentRingtone?.play()
            Log.d("AlarmDebug", "Alarm sound started successfully")
        } else {
            Log.e("AlarmDebug", "Failed to get ringtone")
        }

        // Setting a PendingIntent to open the app with the sound off
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("stopAlarm", true) // Pass a signal to stop the sound
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            alertId.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Setting PendingIntent to turn off audio only
        val stopIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_STOP_ALARM
            putExtra("alertId", alertId)
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            alertId.hashCode(),
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Time format based on language
        val locale = getLocaleForLanguage(language)
        val timeFormat = SimpleDateFormat("HH:mm", locale)
        val formattedTime = timeFormat.format(triggerTime)

        val notification = NotificationCompat.Builder(context, "weather_channel")
            .setSmallIcon(R.drawable.application_icon)
            .setContentTitle(if (language == "ar") "تحديث الطقس" else "Weather Update")
            .setContentText(
                if (language == "ar")
                    "الطقس $weatherDescription في $locationName الساعة $formattedTime"
                else
                    "It's $weatherDescription in $locationName at $formattedTime"
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openPendingIntent)
            .addAction(
                R.drawable.alarm,
                if (language == "ar") "إيقاف المنبه" else "Stop Alarm",
                stopPendingIntent
            )
            .setAutoCancel(true)
            .setOngoing(true)
            .build()

        Log.d("AlarmDebug", "Showing alarm notification for ID: $alertId at ${System.currentTimeMillis()}")
        notificationManager.notify(alertId.hashCode(), notification)
        Log.d("AlarmDebug", "Alarm notification successfully shown for ID: $alertId")
    }

    private fun createNotificationChannel(context: Context, language: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "weather_channel",
                if (language == "ar") "تنبيهات الطقس" else "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = if (language == "ar") "قناة لتنبيهات الطقس" else "Channel for weather alerts"
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            Log.d("AlarmDebug", "Creating notification channel")
            notificationManager.createNotificationChannel(channel)
            Log.d("AlarmDebug", "Notification channel created successfully")
        }
    }

    // Helper function to determine Locale based on language
    private fun getLocaleForLanguage(language: String): Locale {
        return when (language) {
            "ar" -> Locale("ar")
            else -> Locale("en")
        }
    }
}