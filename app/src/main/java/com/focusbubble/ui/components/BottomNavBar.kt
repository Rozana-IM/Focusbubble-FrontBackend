package com.focusbubble.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import com.focusbubble.R

@Composable
fun BottomNavBar(selectedTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(
        containerColor = Color.Transparent // ✅ removes background
    ) {
        NavigationBarItem(
            selected = selectedTab == "Schedules",
            onClick = { onTabSelected("Schedules") },
            icon = {
                Icon(
                    painterResource(R.drawable.ic_timer),
                    contentDescription = "Schedules",
                    tint = Color.White // ✅ white icon
                )
            },
            label = {
                Text(
                    "Schedules",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent // ✅ no purple highlight
            )
        )
        NavigationBarItem(
            selected = selectedTab == "Chats",
            onClick = { onTabSelected("Chats") },
            icon = {
                Icon(
                    painterResource(R.drawable.ic_chat),
                    contentDescription = "Chats",
                    tint = Color.White
                )
            },
            label = {
                Text(
                    "Chats",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = selectedTab == "Blocks",
            onClick = { onTabSelected("Blocks") },
            icon = {
                Icon(
                    painterResource(R.drawable.ic_block),
                    contentDescription = "Blocks",
                    tint = Color.White
                )
            },
            label = {
                Text(
                    "Blocks",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )
    }
}
