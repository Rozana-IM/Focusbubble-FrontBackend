package com.focusbubble.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    userName: String,
    onMenuClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                // Greeting → smaller + slightly transparent
                Text(
                    text = greeting(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 23.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                // Username → larger + cursive "bakery style"
                Text(
                    text = if (userName.isNotBlank()) userName else "User",
                    fontFamily = FontFamily.Cursive,
                    fontSize = 30.sp,
                    color = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = Color.White // keep visible, no background
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent  // background fully transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )
}

private fun greeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    // Debug log to see what your device thinks the hour is
    Log.d("GREETING", "Detected hour = $hour")

    return when (hour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..20 -> "Good Evening"
        else -> "Good Night"
    }
}
