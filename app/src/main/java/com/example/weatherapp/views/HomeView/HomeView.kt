package com.example.weatherapp.views.HomeView

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R

@Preview
@Composable
fun ImageWithOverlayScreen() {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp)),
                containerColor = Color.Gray
            ) {
                IconButton(
                    onClick = { /* TODO: Action 1 */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = R.drawable.home),
                        contentDescription = "Home",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = { /* TODO: Action 2 */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = R.drawable.favorite),
                        contentDescription = "Favorite",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = { /* TODO: Action 3 */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = R.drawable.map),
                        contentDescription = "Map",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = { /* TODO: Action 4 */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = R.drawable.alarm),
                        contentDescription = "Alarm",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = { /* TODO: Action 4 */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = R.drawable.settings),
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.weatherbackground),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Image(
                painter = painterResource(id = R.drawable.house),
                contentDescription = "Overlay",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                    .size(350.dp)
            )


        }
    }
}




