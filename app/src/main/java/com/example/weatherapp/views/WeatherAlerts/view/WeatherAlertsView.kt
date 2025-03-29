package com.example.weatherapp.views.WeatherAlerts.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R
import com.example.weatherapp.models.AlertType
import com.example.weatherapp.models.WeatherAlert
import com.example.weatherapp.ui.theme.CustomFont
import com.example.weatherapp.views.WeatherAlerts.viewModel.WeatherAlertsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherAlertsView(
    viewModel: WeatherAlertsViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val alerts by viewModel.alerts.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xff100b20),
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Weather Alert")
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF1E2A44),
                    contentColor = Color.White,
                    actionColor = Color(0xFF6C61B5),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
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
                IconButton(
                    onClick = onBackClick,
                ) {
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
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.notifications_active),
                    contentDescription = "Weather Notifications",
                    colorFilter = ColorFilter.tint(color = Color(0xFF6C61B5)),
                    modifier = Modifier
                        .size(35.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (alerts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(top = 100.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation5))
                    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(200.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "No Alerts Set Yet. Add One Now !",
                        fontSize = 16.sp,
                        fontFamily = CustomFont,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                LazyColumn {
                    items(alerts.reversed()) { alert ->
                        AlertItem(
                            alert = alert,
                            onDeleteClick = { viewModel.deleteAlert(alert.id) },
                            onUndoClick = { viewModel.undoDeleteAlert() },
                            getDeletedAlertsCount = { viewModel.getDeletedAlertsCount() },
                            snackbarHostState = snackbarHostState,
                            coroutineScope = coroutineScope
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
    onDeleteClick: () -> Unit,
    onUndoClick: suspend () -> Boolean,
    getDeletedAlertsCount: () -> Int,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = alert.triggerTime

    val currentCalendar = Calendar.getInstance()
    val isToday = calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
            calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
            calendar.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH)

    val dayNames = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val dayName = if (isToday) "Today" else dayNames[calendar.get(Calendar.DAY_OF_WEEK) - 1]

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeText = timeFormat.format(Date(alert.triggerTime))
    val dateFormat2 = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateText = dateFormat2.format(Date(alert.triggerTime))

    var showDeleteDialog by remember { mutableStateOf(false) }

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
                    text = "Date: $dateText",
                    color = Color(0xFFB7ACFF),
                    fontSize = 14.sp,
                    fontFamily = CustomFont,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Time: $timeText",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = CustomFont,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dayName,
                        color = Color(0xFF6DFFE7),
                        fontSize = 14.sp,
                        fontFamily = CustomFont,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Type: ${alert.alertType.name}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = CustomFont
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (alert.alertType == AlertType.NOTIFICATION)
                            Icons.Default.Notifications else Icons.Default.Alarm,
                        contentDescription = "Alert Type",
                        tint = if (alert.alertType == AlertType.NOTIFICATION)
                            Color(0xFF9C91FF) else Color(0xFFFF9D5C),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Location",
                    tint = Color.Yellow
                )
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Confirm Deletion",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CustomFont
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Icon",
                        tint = Color.Yellow,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to delete this alert scheduled for ${dateFormat.format(Date(alert.triggerTime))} ?",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = CustomFont
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF6C61B5), shape = MaterialTheme.shapes.small)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable {
                            onDeleteClick()
                            coroutineScope.launch {
                                if (alert.isActive) {
                                    // Alert is active, we display Snackbar with Undo option
                                    val remainingCount = getDeletedAlertsCount()
                                    val message = if (remainingCount > 0) {
                                        "Alert deleted successfully ($remainingCount remaining)"
                                    } else {
                                        "Alert deleted successfully"
                                    }
                                    snackbarHostState.showSnackbar(
                                        message = message,
                                        actionLabel = if (remainingCount > 0) "Undo" else null,
                                        duration = SnackbarDuration.Long
                                    ).let { result ->
                                        if (result == SnackbarResult.ActionPerformed) {
                                            val isActive = onUndoClick()
                                            Log.d("AlertItem", "After undo, isActive: $isActive")
                                            val newRemainingCount = getDeletedAlertsCount()
                                            val undoMessage = if (isActive) {
                                                if (newRemainingCount > 0) {
                                                    "Alert restored successfully"
                                                } else {
                                                    "Alert restored successfully"
                                                }
                                            } else {
                                                if (newRemainingCount > 0) {
                                                    "Alert restored but expired"
                                                } else {
                                                    "Alert restored but expired"
                                                }
                                            }
                                            snackbarHostState.showSnackbar(
                                                message = undoMessage,
                                                actionLabel = if (newRemainingCount > 0) "Undo" else null,
                                                duration = SnackbarDuration.Long
                                            )
                                        }
                                    }
                                } else {
                                    // The alert has expired, we are displaying the Snackbar without the Undo option.
                                    snackbarHostState.showSnackbar(
                                        message = "Expired alert deleted successfully",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                            showDeleteDialog = false
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Yes",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = CustomFont
                    )
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFB0B0B0), shape = MaterialTheme.shapes.small)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable { showDeleteDialog = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontFamily = CustomFont
                    )
                }
            },
            containerColor = Color(0xFF1E2A44),
            shape = MaterialTheme.shapes.medium
        )
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

    fun createDatePickerDialog(): DatePickerDialog {
        return DatePickerDialog(
            context,
            R.style.CustomDatePickerDialog,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val timePickerDialog = TimePickerDialog(
                    context,
                    R.style.CustomTimePickerDialog,
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
                ).apply {
                    setOnCancelListener {
                        createDatePickerDialog().show()
                    }
                }
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Weather Alert",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CustomFont,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.AddAlert,
                    contentDescription = "Add Weather Alert",
                    tint = Color(0xFF6C61B5),
                    modifier = Modifier
                        .size(32.dp)
                        .padding(bottom = 8.dp)
                )
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = timeText,
                    onValueChange = { },
                    label = { Text("Alert Time", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { createDatePickerDialog().show() },
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
                Text(
                    text = "Alert Type :",
                    color = Color.White,
                    fontFamily = CustomFont,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        RadioButton(
                            modifier = Modifier.size(16.dp),
                            selected = selectedType == AlertType.NOTIFICATION,
                            onClick = { selectedType = AlertType.NOTIFICATION },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF6C61B5))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Notification",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontFamily = CustomFont
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Weather Notifications",
                                tint = Color(0xFF6C61B5),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        RadioButton(
                            modifier = Modifier.size(16.dp),
                            selected = selectedType == AlertType.ALARM,
                            onClick = { selectedType = AlertType.ALARM },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF6C61B5))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Alarm",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontFamily = CustomFont
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = "Weather Alarm",
                                tint = Color(0xFF6C61B5),
                                modifier = Modifier.size(18.dp)
                            )
                        }
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