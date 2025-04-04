package com.example.weatherapp.views.Favourite.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.core.content.ContextCompat
import com.example.weatherapp.models.FavoriteLocation
import com.example.weatherapp.ui.theme.CustomFont
import com.example.weatherapp.utils.ScreenRoute
import com.example.weatherapp.views.Favourite.ViewModel.FavoritesViewModel
import com.example.weatherapp.views.Home.ViewModel.HomeViewModel
import com.example.weatherapp.views.Home.ViewModel.LocationSource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Map
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.*
import com.example.weatherapp.R
import androidx.navigation.NavHostController
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.weatherapp.views.Map.MapSelectionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesView(
    language: String,
    viewModel: FavoritesViewModel,
    onMapClick: () -> Unit,
    onBackClick: () -> Unit,
    navController: NavHostController,
    homeViewModel: HomeViewModel
) {
    val favoriteLocations by viewModel.favoriteLocations.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val locationSource by homeViewModel.locationSource.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Update cityNameAr and cityNameEn when FavoritesView is opened if they are empty
    LaunchedEffect(favoriteLocations) {
        favoriteLocations.forEach { location ->
            if (location.cityNameAr.isBlank() || location.cityNameEn.isBlank()) {
                val cityNameAr = homeViewModel.fetchCityNameFromApi(location.latitude, location.longitude, "ar")
                val cityNameEn = homeViewModel.fetchCityNameFromApi(location.latitude, location.longitude, "en")
                if (!cityNameAr.startsWith("Error:") && !cityNameEn.startsWith("Error:")) {
                    val updatedLocation = location.copy(
                        cityNameAr = cityNameAr,
                        cityNameEn = cityNameEn
                    )
                    viewModel.updateFavoriteLocation(updatedLocation)
                }
            }
        }
    }

    val layoutDirection = if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            containerColor = Color(0xff100b20),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (locationSource == LocationSource.MAP) {
                            onMapClick()
                        } else {
                            showSettingsDialog = true
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = if (language == "ar") "إضافة موقع مفضل" else "Add Favorite Location"
                    )
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
                        if (language == "ar") {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "رجوع",
                                tint = Color(0xFF6C61B5),
                                modifier = Modifier.size(28.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF6C61B5),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "ar") "مدنك المفضلة" else "Your Favorite Cities",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CustomFont,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = if (language == "ar") "موقع مفضل" else "Favorite Location",
                        tint = Color(0xFF6C61B5),
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (favoriteLocations.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(top = 100.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation2))
                        val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.size(200.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (language == "ar") "لم يتم إضافة مدن بعد. أضف مدينتك الآن!" else "No Cities Added Yet. Add Your City Now!",
                            fontSize = 16.sp,
                            fontFamily = CustomFont,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(paddingValues)
                    ) {
                        items(favoriteLocations.reversed()) { location ->
                            FavoriteLocationItem(
                                language = language,
                                location = location,
                                homeViewModel = homeViewModel,
                                navController = navController,
                                onItemClick = {
                                    coroutineScope.launch {
                                        val geocoder = android.location.Geocoder(context)
                                        homeViewModel.fetchWeatherData(location.latitude, location.longitude, geocoder)
                                        delay(3000)
                                        navController.navigate(ScreenRoute.HomeViewRoute.route) {
                                            popUpTo(ScreenRoute.HomeViewRoute.route) { inclusive = true }
                                        }
                                    }
                                },
                                onDeleteClick = { viewModel.removeFavoriteLocation(it) },
                                onUndoClick = { viewModel.addFavoriteLocation(it) },
                                snackbarHostState = snackbarHostState,
                                coroutineScope = coroutineScope
                            )
                        }
                    }
                }
            }
        }
    }

    // Settings Prompt Dialog for FloatingActionButton
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == "ar") "تفعيل الخريطة" else "Enable Map",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CustomFont
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = if (language == "ar") "أيقونة الخريطة" else "Map Icon",
                        tint = Color(0xFF6C61B5),
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            text = {
                Text(
                    text = if (language == "ar") "تحتاج إلى تفعيل 'الخريطة' في الإعدادات لإضافة مواقع مفضلة." else "You need to enable 'Map' in Settings to add favorite locations.",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = CustomFont
                )
            },
            confirmButton = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF6C61B5), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable {
                                navController.navigate(ScreenRoute.SettingViewRoute.route)
                                showSettingsDialog = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (language == "ar") "الذهاب إلى الإعدادات" else "Go to Settings",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = CustomFont
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                            .background(Color(0xFFB0B0B0), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable { showSettingsDialog = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (language == "ar") "إلغاء" else "Cancel",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = CustomFont
                        )
                    }
                }
            },
            dismissButton = { },
            containerColor = Color(0xFF1E2A44),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteLocationItem(
    language: String,
    location: FavoriteLocation,
    homeViewModel: HomeViewModel,
    navController: NavHostController,
    onItemClick: () -> Unit,
    onDeleteClick: (FavoriteLocation) -> Unit,
    onUndoClick: (FavoriteLocation) -> Unit,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    val locationSource by homeViewModel.locationSource.collectAsState()

    // Function to convert numbers to Arabic numerals
    fun String.toLocalizedFormat(): String {
        if (language != "ar") return this
        val arabicDigits = "٠١٢٣٤٥٦٧٨٩"
        val westernDigits = "0123456789"
        var result = this
        for (i in westernDigits.indices) {
            result = result.replace(westernDigits[i], arabicDigits[i])
        }
        return result
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                if (locationSource == LocationSource.MAP) {
                    homeViewModel.setLanguage(language)
                    onItemClick()
                } else {
                    showSettingsDialog = true
                }
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A44)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = if (language == "ar") "أيقونة الموقع" else "Location Icon",
                    tint = Color(0xFF6C61B5),
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = if (language == "ar") {
                            if (location.cityNameAr.isNotBlank()) location.cityNameAr else "موقع غير معروف"
                        } else {
                            if (location.cityNameEn.isNotBlank()) location.cityNameEn else "Unknown Location"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CustomFont,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (language == "ar") {
                            "خطوط الطول والعرض (${location.latitude.toString().toLocalizedFormat()}, ${location.longitude.toString().toLocalizedFormat()})"
                        } else {
                            "lat & lon (${location.latitude}, ${location.longitude})"
                        },
                        fontSize = 14.sp,
                        fontFamily = CustomFont,
                        color = Color(0xFFB0B0B0)
                    )
                }
            }

            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = if (language == "ar") "حذف الموقع" else "Delete Location",
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
                        text = if (language == "ar") "تأكيد الحذف" else "Confirm Deletion",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CustomFont
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = if (language == "ar") "أيقونة الحذف" else "Delete Icon",
                        tint = Color.Yellow,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            text = {
                Text(
                    text = if (language == "ar") "هل أنت متأكد أنك تريد حذف ${location.cityNameAr}؟" else "Are you sure you want to delete ${location.cityNameEn}?",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = CustomFont
                )
            },
            confirmButton = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF6C61B5), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable {
                                onDeleteClick(location)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = if (language == "ar") "تم حذف الموقع بنجاح" else "Location deleted successfully",
                                        actionLabel = if (language == "ar") "تراجع" else "Undo",
                                        duration = SnackbarDuration.Long
                                    ).let { result ->
                                        if (result == SnackbarResult.ActionPerformed) {
                                            onUndoClick(location)
                                            snackbarHostState.showSnackbar(
                                                message = if (language == "ar") "تم استعادة الموقع بنجاح" else "Location restored successfully",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                }
                                showDeleteDialog = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (language == "ar") "نعم" else "Yes",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = CustomFont
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFB0B0B0), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable { showDeleteDialog = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (language == "ar") "لا" else "No",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = CustomFont
                        )
                    }
                }
            },
            dismissButton = { },
            containerColor = Color(0xFF1E2A44),
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Settings Prompt Dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == "ar") "تفعيل الخريطة" else "Enable Map",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CustomFont
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = if (language == "ar") "أيقونة الخريطة" else "Map Icon",
                        tint = Color(0xFF6C61B5),
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            text = {
                Text(
                    text = if (language == "ar") "تحتاج إلى تفعيل 'الخريطة' في الإعدادات لعرض الطقس من المواقع المفضلة." else "You need to enable 'Map' in Settings to view weather from favorite locations.",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = CustomFont
                )
            },
            confirmButton = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF6C61B5), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable {
                                navController.navigate(ScreenRoute.SettingViewRoute.route)
                                showSettingsDialog = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (language == "ar") "الذهاب إلى الإعدادات" else "Go to Settings",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = CustomFont
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFB0B0B0), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable { showSettingsDialog = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (language == "ar") "إلغاء" else "Cancel",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = CustomFont
                        )
                    }
                }
            },
            dismissButton = {},
            containerColor = Color(0xFF1E2A44),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSelectionView(
    language: String,
    viewModel: MapSelectionViewModel,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var locationName by remember { mutableStateOf("") }
    var cameraPosition by remember {
        mutableStateOf(CameraPosition.fromLatLngZoom(LatLng(31.0, 31.0), 3f))
    }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasLocationPermission = isGranted }

    var searchQuery by remember { mutableStateOf("") }
    val searchSuggestions by viewModel.searchSuggestions.collectAsState()
    var isSaved by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val layoutDirection = if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            containerColor = Color(0xff100b20),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xff100b20)
                    ),
                    title = {
                        Text(
                            text = if (language == "ar") "اختر موقعًا" else "Select Location",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CustomFont,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onBackClick() }) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = if (language == "ar") "رجوع" else "Back",
                                tint = Color(0xFF6C61B5),
                            )
                        }
                    }
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 60.dp, top = 20.dp, start = 40.dp, end = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .size(48.dp)
                            .border(2.dp, Color(0xFF6C61B5), CircleShape),
                        shape = CircleShape
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            IconButton(
                                onClick = {
                                    selectedLocation?.let { latLng ->
                                        val favoriteLocation = FavoriteLocation(
                                            latitude = latLng.latitude,
                                            longitude = latLng.longitude,
                                            cityNameAr = "",
                                            cityNameEn = ""
                                        )
                                        coroutineScope.launch {
                                            viewModel.addFavoriteLocation(favoriteLocation)
                                            isSaved = true
                                            onBackClick()
                                        }
                                    }
                                },
                                enabled = selectedLocation != null,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (language == "ar") "حفظ" else "Save",
                                    tint = if (isSaved) Color.Red else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                GoogleMapWithMarker(
                    language = language,
                    cameraPosition = cameraPosition,
                    selectedLocation = selectedLocation,
                    onMapClick = { clickedLocation ->
                        selectedLocation = clickedLocation
                        isSaved = false
                        // Use Geocoder to retrieve the name of the coordinate
                        coroutineScope.launch {
                            val geocoder = android.location.Geocoder(context, if (language == "ar") Locale("ar") else Locale.getDefault())
                            val addresses = geocoder.getFromLocation(clickedLocation.latitude, clickedLocation.longitude, 1)
                            val address = addresses?.firstOrNull()
                            locationName = if (address != null) {
                                val cityName = address.locality ?: address.adminArea ?: "Unknown"
                                if (language == "ar") {
                                    val latStr = clickedLocation.latitude.toString().toLocalizedFormat(language)
                                    val lonStr = clickedLocation.longitude.toString().toLocalizedFormat(language)
                                    "$cityName - خط العرض وطول العرض ($latStr, $lonStr)"
                                } else {
                                    "$cityName - lat & lon (${clickedLocation.latitude}, ${clickedLocation.longitude})"
                                }
                            } else {
                                if (language == "ar") {
                                    val latStr = clickedLocation.latitude.toString().toLocalizedFormat(language)
                                    val lonStr = clickedLocation.longitude.toString().toLocalizedFormat(language)
                                    "خط العرض وطول العرض ($latStr, $lonStr)"
                                } else {
                                    "lat & lon (${clickedLocation.latitude}, ${clickedLocation.longitude})"
                                }
                            }
                        }
                        cameraPosition = CameraPosition.fromLatLngZoom(clickedLocation, 9f)
                    },
                    onCameraMove = { position -> cameraPosition = position }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { newValue ->
                            searchQuery = newValue
                            viewModel.searchLocation(newValue)
                            isSaved = false
                        },
                        placeholder = { Text(if (language == "ar") "ابحث عن مدينة" else "Search City") },
                        singleLine = true,
                        modifier = Modifier
                            .width(250.dp)
                            .zIndex(1f),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF6C61B5),
                            unfocusedBorderColor = Color.Gray,
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search,
                            autoCorrect = language == "ar"
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            fontFamily = CustomFont,
                            textDirection = if (language == "ar") TextDirection.Rtl else TextDirection.Ltr
                        )
                    )

                    if (searchSuggestions.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .background(Color.White)
                                .padding(8.dp)
                                .heightIn(max = 200.dp)
                                .zIndex(1f)
                        ) {
                            items(searchSuggestions) { suggestion ->
                                Text(
                                    text = suggestion,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable {
                                            coroutineScope.launch {
                                                val geocoder = android.location.Geocoder(context, if (language == "ar") Locale("ar") else Locale.getDefault())
                                                val address = geocoder.getFromLocationName(suggestion, 1)?.firstOrNull()
                                                address?.let {
                                                    val latLng = LatLng(it.latitude, it.longitude)
                                                    selectedLocation = latLng
                                                    locationName = if (language == "ar") {
                                                        val latStr = latLng.latitude.toString().toLocalizedFormat(language)
                                                        val lonStr = latLng.longitude.toString().toLocalizedFormat(language)
                                                        "خط العرض وطول العرض ($latStr, $lonStr)"
                                                    } else {
                                                        "lat & lon (${latLng.latitude}, ${latLng.longitude})"
                                                    }
                                                    cameraPosition = CameraPosition.fromLatLngZoom(latLng, 9f)
                                                    isSaved = false
                                                }
                                            }
                                            searchQuery = ""
                                            viewModel.searchLocation("")
                                        }
                                )
                            }
                        }
                    }

                    if (locationName.isNotEmpty()) {
                        Text(
                            text = locationName,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontFamily = CustomFont,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .background(Color(0xFF1E2A44), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                                .zIndex(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleMapWithMarker(
    language: String,
    cameraPosition: CameraPosition,
    selectedLocation: LatLng?,
    onMapClick: (LatLng) -> Unit,
    onCameraMove: (CameraPosition) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = cameraPosition
    }

    LaunchedEffect(cameraPosition) {
        cameraPositionState.position = cameraPosition
    }

    val mapProperties = MapProperties(isMyLocationEnabled = true)
    val mapUiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = true)

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = mapProperties,
        uiSettings = mapUiSettings,
        cameraPositionState = cameraPositionState,
        onMapClick = onMapClick
    ) {
        selectedLocation?.let { location ->
            Marker(
                state = MarkerState(position = location),
                title = if (language == "ar") "الموقع المختار" else "Selected Location"
            )
        }
    }
}

// Function to convert numbers to local format based on language
fun String.toLocalizedFormat(language: String): String {
    var result = this
    if (language == "ar") {
        val arabicDigits = "٠١٢٣٤٥٦٧٨٩"
        val westernDigits = "0123456789"
        for (i in westernDigits.indices) {
            result = result.replace(westernDigits[i], arabicDigits[i])
        }
    }
    return result
}