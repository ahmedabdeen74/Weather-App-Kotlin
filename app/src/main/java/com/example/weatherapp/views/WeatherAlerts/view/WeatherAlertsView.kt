package com.example.weatherapp.views.WeatherAlerts.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.models.AlertType
import com.example.weatherapp.models.WeatherAlert
import com.example.weatherapp.ui.theme.CustomFont
import com.example.weatherapp.utils.AlarmReceiver
import com.example.weatherapp.views.WeatherAlerts.viewModel.WeatherAlertsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherAlertsView(
    viewModel: WeatherAlertsViewModel,
    onBackClick: () -> Unit
) {
    val alerts by viewModel.alerts.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xff100b20),
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Weather Alert", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF6C61B5),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Weather Alerts",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CustomFont,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (alerts.isEmpty()) {
                Text(
                    text = "No alerts set yet. Add one now!",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = CustomFont,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn {
                    items(alerts) { alert ->
                        AlertItem(
                            alert = alert,
                            onDisableClick = { viewModel.disableAlert(alert.id) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddAlertDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { triggerTime, type ->
                    viewModel.addWeatherAlert(triggerTime, type)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AlertItem(
    alert: WeatherAlert,
    onDisableClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val triggerTimeText = dateFormat.format(Date(alert.triggerTime))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A44)),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Time: $triggerTimeText",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = CustomFont,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Type: ${alert.alertType.name}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = CustomFont
                )
            }
            if (alert.isActive) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (alert.alertType == AlertType.SOUND && System.currentTimeMillis() >= alert.triggerTime) {
                        OutlinedButton(
                            onClick = { AlarmReceiver.stopAlarmSound() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Stop Sound", fontSize = 12.sp, fontFamily = CustomFont)
                        }
                    }
                    Button(
                        onClick = onDisableClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C61B5)),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Disable", fontSize = 12.sp, fontFamily = CustomFont)
                    }
                }
            } else {
                Text(
                    text = "Disabled",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontFamily = CustomFont,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlertDialog(
    onDismiss: () -> Unit,
    onAdd: (Long, AlertType) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var selectedTime by remember { mutableStateOf<Long?>(null) }
    var selectedType by remember { mutableStateOf(AlertType.NOTIFICATION) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val timeText = selectedTime?.let { dateFormat.format(Date(it)) } ?: "Select Date & Time"
    val currentCalendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            val timePickerDialog = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    val newTime = calendar.timeInMillis
                    val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
                    val currentMinute = currentCalendar.get(Calendar.MINUTE)
                    val isSameDay = calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                            calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
                            calendar.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH)

                    when {
                        newTime <= System.currentTimeMillis() -> {
                            errorMessage = "Selected time is in the past"
                        }
                        isSameDay && hourOfDay < currentHour -> {
                            errorMessage = "Hour cannot be earlier than current hour ($currentHour)"
                        }
                        isSameDay && hourOfDay == currentHour && minute < currentMinute -> {
                            errorMessage = "Minutes cannot be earlier than current minute ($currentMinute)"
                        }
                        else -> {
                            selectedTime = newTime
                            errorMessage = null
                        }
                    }
                },
                currentCalendar.get(Calendar.HOUR_OF_DAY),
                currentCalendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = System.currentTimeMillis() - 1000 // Prevent selecting a date before today
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Weather Alert",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CustomFont,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = timeText,
                    onValueChange = {  },
                    label = { Text("Alert Time", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    enabled = false,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = if (selectedTime == null) Color.Gray else Color.White,
                        focusedBorderColor = Color(0xFF6C61B5),
                        unfocusedBorderColor = Color.Gray,
                        disabledBorderColor = Color.Gray
                    )
                )
                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Alert Type:", color = Color.White, fontFamily = CustomFont)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedType == AlertType.NOTIFICATION,
                            onClick = { selectedType = AlertType.NOTIFICATION },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF6C61B5))
                        )
                        Text("Notification", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedType == AlertType.SOUND,
                            onClick = { selectedType = AlertType.SOUND },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF6C61B5))
                        )
                        Text("Sound", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        selectedTime == null -> {
                            errorMessage = "You must select a date and time"
                        }
                        selectedTime!! <= System.currentTimeMillis() -> {
                            errorMessage = "Selected time is in the past"
                        }
                        else -> {
                            onAdd(selectedTime!!, selectedType)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C61B5)),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Add", fontSize = 14.sp, fontFamily = CustomFont)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Cancel", fontSize = 14.sp, fontFamily = CustomFont)
            }
        },
        containerColor = Color(0xFF1E2A44),
        modifier = Modifier.padding(16.dp)
    )
}