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
import androidx.compose.material.icons.filled.Map
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.*
import com.example.weatherapp.R
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesView(
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
                Icon(Icons.Default.Add, contentDescription = "Add Favorite Location")
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
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
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
                    text = "Your Favorite Cities",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CustomFont,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite Location",
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
                        text = "No Cities Added Yet. Add Your City Now !",
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
                            location = location,
                            homeViewModel = homeViewModel,
                            navController = navController,
                            onItemClick = {
                                val geocoder = android.location.Geocoder(context)
                                homeViewModel.fetchWeatherData(location.latitude, location.longitude, geocoder)
                                navController.navigate(ScreenRoute.HomeViewRoute.route) {
                                    popUpTo(ScreenRoute.HomeViewRoute.route) { inclusive = true }
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

    // Settings Prompt Dialog for FloatingActionButton
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enable Map",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CustomFont
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Map Icon",
                        tint = Color(0xFF6C61B5),
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            text = {
                Text(
                    text = "You need to enable 'Map' in Settings to add favorite locations.",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = CustomFont
                )
            },
            confirmButton = {
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
                        text = "Go to Settings",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = CustomFont
                    )
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFB0B0B0), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable { showSettingsDialog = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontFamily = CustomFont
                    )
                }
            },
            containerColor = Color(0xFF1E2A44),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteLocationItem(
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                if (locationSource == LocationSource.MAP) {
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
                    contentDescription = "Location Icon",
                    tint = Color(0xFF6C61B5),
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = location.name.split("\n")[0],
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CustomFont,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "lat & lon (${location.latitude}, ${location.longitude})",
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
                    text = "Are you sure you want to delete ${location.name.split("\n")[0]} ?",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = CustomFont
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF6C61B5), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable {
                            onDeleteClick(location)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Location deleted successfully",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Long
                                ).let { result ->
                                    if (result == SnackbarResult.ActionPerformed) {
                                        onUndoClick(location)
                                        snackbarHostState.showSnackbar(
                                            message = "Location restored successfully",
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
                        .background(Color(0xFFB0B0B0), shape = RoundedCornerShape(8.dp))
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
                        text = "Enable Map",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CustomFont
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Map Icon",
                        tint = Color(0xFF6C61B5),
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            text = {
                Text(
                    text = "You need to enable 'Map' in Settings to view weather from favorite locations.",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = CustomFont
                )
            },
            confirmButton = {
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
                        text = "Go to Settings",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = CustomFont
                    )
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFB0B0B0), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable { showSettingsDialog = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontFamily = CustomFont
                    )
                }
            },
            containerColor = Color(0xFF1E2A44),
            shape = RoundedCornerShape(12.dp)
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSelectionView(
    viewModel: FavoritesViewModel,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var locationName by remember { mutableStateOf("") }
    var cityName by remember { mutableStateOf("") }
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

    Scaffold(
        containerColor = Color(0xff100b20),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xff100b20)
                ),
                title = {
                    Text(
                        "Select Location",
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
                            contentDescription = "Back",
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
                    .padding(16.dp),
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
                                        name = locationName.ifEmpty { "Unnamed Location" },
                                        latitude = latLng.latitude,
                                        longitude = latLng.longitude
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
                                contentDescription = "Save",
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
                cameraPosition = cameraPosition,
                selectedLocation = selectedLocation,
                onMapClick = { clickedLocation ->
                    selectedLocation = clickedLocation
                    isSaved = false
                    coroutineScope.launch {
                        cityName = viewModel.getCityNameFromLatLng(clickedLocation)
                        locationName = "$cityName\n\nlat & lon (${clickedLocation.latitude}, ${clickedLocation.longitude})"
                        cameraPosition = CameraPosition.fromLatLngZoom(clickedLocation, 4f)
                    }
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
                    placeholder = { Text("Search City") },
                    singleLine = true,
                    modifier = Modifier
                        .width(250.dp)
                        .zIndex(1f),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF6C61B5),
                        unfocusedBorderColor = Color.Gray,
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
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
                                            val geocoder = android.location.Geocoder(context)
                                            val address = geocoder.getFromLocationName(suggestion, 1)?.firstOrNull()
                                            address?.let {
                                                val latLng = LatLng(it.latitude, it.longitude)
                                                selectedLocation = latLng
                                                cityName = suggestion
                                                locationName = "$cityName\n\nlat & lon (${latLng.latitude}, ${latLng.longitude})"
                                                cameraPosition = CameraPosition.fromLatLngZoom(latLng, 15f)
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
            }
        }
    }
}

@Composable
fun GoogleMapWithMarker(
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
                title = "Selected Location"
            )
        }
    }
}
