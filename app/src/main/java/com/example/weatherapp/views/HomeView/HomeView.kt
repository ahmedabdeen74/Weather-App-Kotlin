package com.example.weatherapp.views.HomeView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.CustomFont


enum class SheetState {
    COLLAPSED, EXPANDED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView() {

    val bottomAppBarHeight = 100.dp
    var sheetState by remember { mutableStateOf(SheetState.COLLAPSED) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenHeight = maxHeight

        Scaffold(
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier
                        .height(bottomAppBarHeight)
                        .clip(RoundedCornerShape(24.dp)),
                    containerColor = Color(0xFF1B0D67).copy(alpha = 0.7f)
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
                    .background(Color(0xFF1B0D67).copy(alpha = 0.7f))
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
                        .padding(bottom = 16.dp)
                        .size(350.dp)
                )

                PermanentBottomSheetContent(
                    sheetState = sheetState,
                    onSheetStateChange = { newState ->
                        sheetState = newState
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
fun PermanentBottomSheetContent(
    sheetState: SheetState,
    onSheetStateChange: (SheetState) -> Unit,
    modifier: Modifier = Modifier
) {
    val topPartHeight = when (sheetState) {
        SheetState.COLLAPSED -> 280.dp
        SheetState.EXPANDED -> 500.dp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(topPartHeight)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color(0xFF1B0D67).copy(alpha = 0.7f))
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    if (dragAmount < 0 && sheetState != SheetState.EXPANDED) {

                        onSheetStateChange(SheetState.EXPANDED)
                    } else if (dragAmount > 0 && sheetState != SheetState.COLLAPSED) {
                        onSheetStateChange(SheetState.COLLAPSED)
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }

        if (sheetState == SheetState.EXPANDED) {
            IconButton(
                onClick = { onSheetStateChange(SheetState.COLLAPSED) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.cancel),
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}