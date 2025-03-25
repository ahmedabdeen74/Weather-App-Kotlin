//package com.example.weatherapp.views.Favourite.View
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.example.weatherapp.models.FavoriteLocation
//import com.example.weatherapp.views.Favourite.FavoritesViewModel
//import com.google.android.gms.maps.model.CameraPosition
//import com.google.android.gms.maps.model.LatLng
//import com.google.maps.android.compose.GoogleMap
//import com.google.maps.android.compose.MapProperties
//import com.google.maps.android.compose.MapUiSettings
//import com.google.maps.android.compose.Marker
//import com.google.maps.android.compose.MarkerState
//import com.google.maps.android.compose.rememberCameraPositionState
//import kotlinx.coroutines.launch
//import android.Manifest
//import android.content.pm.PackageManager
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.*
//
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.LocationOn
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.core.content.ContextCompat
//
//import com.google.maps.android.compose.*
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FavoritesView(
//    viewModel: FavoritesViewModel,
//    navController: NavController
//) {
//    val favoriteLocations by viewModel.favoriteLocations.collectAsState()
//    val coroutineScope = rememberCoroutineScope()
//
//    Scaffold(
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = {
//                    // Navigate to Map Selection Screen
//                    navController.navigate("map_selection")
//                }
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Add Favorite Location")
//            }
//        }
//    ) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//            items(favoriteLocations) { location ->
//                FavoriteLocationItem(
//                    location = location,
//                    onItemClick = {
////                        // Navigate to Forecast Details
////                        navController.navigate("forecast_details/${location.latitude}/${location.longitude}")
//                    },
//                    onDeleteClick = {
//                        coroutineScope.launch {
//                            viewModel.removeFavoriteLocation(location)
//                        }
//                    }
//                )
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FavoriteLocationItem(
//    location: FavoriteLocation,
//    onItemClick: () -> Unit,
//    onDeleteClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp),
//        onClick = onItemClick
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(text = location.name)
//            IconButton(onClick = onDeleteClick) {
//                Icon(Icons.Default.Delete, contentDescription = "Delete Location")
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MapSelectionScreen(
//    viewModel: FavoritesViewModel,
//    navController: NavController
//) {
//    val context = LocalContext.current
//    val coroutineScope = rememberCoroutineScope()
//
//    // Location and Map State
//    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
//    var locationName by remember { mutableStateOf("") }
//    var cameraPosition by remember {
//        mutableStateOf(
//            CameraPosition.fromLatLngZoom(
//                LatLng(31.0, 31.0), 6f
//            )
//        )
//    }
//
//    // Permissions
//    var hasLocationPermission by remember {
//        mutableStateOf(
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        )
//    }
//
//    // Permission Launcher
//    val permissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        hasLocationPermission = isGranted
//    }
//
//    // Request Location Permission
//    LaunchedEffect(Unit) {
//        if (!hasLocationPermission) {
//            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Select Location") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.navigateUp() }) {
//                        Icon(Icons.Default.LocationOn, contentDescription = "Back")
//                    }
//                }
//            )
//        },
//        bottomBar = {
//            Button(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                enabled = selectedLocation != null,
//                onClick = {
//                    selectedLocation?.let { latLng ->
//                        val favoriteLocation = FavoriteLocation(
//                            name = locationName.ifEmpty { "Unnamed Location" },
//                            latitude = latLng.latitude,
//                            longitude = latLng.longitude
//                        )
//                        coroutineScope.launch {
//                            viewModel.addFavoriteLocation(favoriteLocation)
//                            navController.navigateUp()
//                        }
//                    }
//                }
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Save")
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("Save Location")
//            }
//        }
//    ) { paddingValues ->
//        Box(modifier = Modifier.padding(paddingValues)) {
//            // Google Maps Composable
//            GoogleMapWithMarker(
//                cameraPosition = cameraPosition,
//                onMapClick = { clickedLocation ->
//                    selectedLocation = clickedLocation
//                    locationName = "Custom Location (${clickedLocation.latitude}, ${clickedLocation.longitude})"
//                },
//                onCameraMove = { position ->
//                    cameraPosition = position
//                }
//            )
//
//            // Manual Location Input
//            var manualLocationInput by remember { mutableStateOf("") }
//
//            OutlinedTextField(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//                    .align(Alignment.TopCenter),
//                value = manualLocationInput,
//                onValueChange = { manualLocationInput = it },
//                label = { Text("Enter Coordinates (Lat,Lon)") },
//                placeholder = { Text("31.0,31.0") },
//                trailingIcon = {
//                    IconButton(
//                        onClick = {
//                            try {
//                                val (lat, lon) = manualLocationInput.split(",").map { it.trim().toDouble() }
//                                val latLng = LatLng(lat, lon)
//                                selectedLocation = latLng
//                                locationName = "Manual Location ($lat, $lon)"
//                                cameraPosition = CameraPosition.fromLatLngZoom(latLng, 15f)
//                            } catch (e: Exception) {
//                                // Handle parsing error
//                            }
//                        }
//                    ) {
//                        Icon(Icons.Default.LocationOn, contentDescription = "Set Location")
//                    }
//                }
//            )
//        }
//    }
//}
//
//@Composable
//fun GoogleMapWithMarker(
//    cameraPosition: CameraPosition,
//    onMapClick: (LatLng) -> Unit,
//    onCameraMove: (CameraPosition) -> Unit
//) {
//    val mapProperties = MapProperties(
//        isMyLocationEnabled = true
//    )
//    val mapUiSettings = MapUiSettings(
//        zoomControlsEnabled = true,
//        myLocationButtonEnabled = true
//    )
//
//    GoogleMap(
//        modifier = Modifier.fillMaxSize(),
//        properties = mapProperties,
//        uiSettings = mapUiSettings,
//        cameraPositionState = rememberCameraPositionState {
//            position = cameraPosition
//        },
//        onMapClick = onMapClick,
//        onCameraMove = { cameraPosition ->
//            onCameraMove(cameraPosition.position)
//        }
//    ) {
//        // Add a marker if a location is selected
//        selectedLocation?.let { location ->
//            Marker(
//                state = MarkerState(position = location),
//                title = "Selected Location"
//            )
//        }
//    }
//}