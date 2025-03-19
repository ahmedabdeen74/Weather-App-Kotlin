package com.example.weatherapp.views.HomeView

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.weatherapp.ui.theme.CustomFont


@Preview
@Composable
fun HomeView() {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp)),
                containerColor = Color(0xFF1B0D67).copy(alpha = 0.7f) // bottom app bar color
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
                    onClick = { /* TODO: Action 5 */ },
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
                .background(Color(0xFF1B0D67).copy(alpha = 0.7f)) // background color
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.weatherbackground),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 65.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Montreal",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CustomFont,
                    color = Color.White
                )
                Text(
                    text = "19°",
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Thin,
                   // fontFamily = CustomFont,
                    color = Color.White
                )
                Text(
                    text = "Mostly Clear",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = CustomFont,
                    color = Color.Gray
                )
                Text(
                    text = "H:24°  L:18°",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = CustomFont,
                    color = Color.White
                )
            }

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
