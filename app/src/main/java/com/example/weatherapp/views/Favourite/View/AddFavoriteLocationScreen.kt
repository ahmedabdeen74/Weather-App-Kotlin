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
import com.example.weatherapp.views.Favourite.ViewModel.FavoritesViewModel
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
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesView(
    viewModel: FavoritesViewModel,
    onMapClick: () -> Unit,
) {
    val favoriteLocations by viewModel.favoriteLocations.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xff100b20),
        floatingActionButton = {
            FloatingActionButton(onClick = { onMapClick() }) {
                Icon(Icons.Default.Add, contentDescription = "Add Favorite Location")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(favoriteLocations) { location ->
                FavoriteLocationItem(
                    location = location,
                    onItemClick = {},
                    onDeleteClick = {
                        coroutineScope.launch {
                            viewModel.removeFavoriteLocation(location)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteLocationItem(
    location: FavoriteLocation,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = onItemClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = location.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CustomFont,
            )
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Location",
                    tint = Color.Yellow
                )
            }
        }
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
    var isSaved by remember { mutableStateOf(false) } // حالة الحفظ

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
                            tint = Color(108, 97, 181),
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
                        .border(2.dp, Color(108, 97, 181), CircleShape),
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
                        focusedBorderColor = Color(108, 97, 181),
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