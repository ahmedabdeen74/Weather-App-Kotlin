//package com.example.weatherapp.views.Favourite.View
//
//import com.example.weatherapp.views.Favourite.FavoritesViewModel
//
//
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
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FavoritesView(
//    viewModel: FavoritesViewModel,
//    navController: NavController
//) {
//    val favorites by viewModel.favorites.collectAsState()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Favorite Locations") }
//            )
//        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = {
////                    // Navigate to add new favorite location screen
////                    navController.navigate("add_favorite")
//                }
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Add Favorite")
//            }
//        }
//    ) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxSize()
//        ) {
//            items(favorites) { location ->
//                FavoriteLocationItem(
//                    location = location,
//                    onItemClick = {
////                        viewModel.navigateToForecast(location)
////                        // Navigate to forecast screen
////                        navController.navigate("forecast/${location.latitude}/${location.longitude}")
//                    },
//                    onDeleteClick = {
//                        viewModel.removeFavorite(location)
//                    }
//                )
//            }
//        }
//    }
//}
//
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
//                .padding(16.dp)
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = location.name,
//                style = MaterialTheme.typography.bodyLarge
//            )
//            IconButton(onClick = onDeleteClick) {
//                Icon(
//                    Icons.Default.Delete,
//                    contentDescription = "Delete Favorite"
//                )
//            }
//        }
//    }
//}