package com.focusbubble.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.focusbubble.R
import com.focusbubble.ui.components.TopBar
import com.focusbubble.ui.components.BottomNavBar
import com.focusbubble.ui.sheets.EditOptionsSheet
import com.focusbubble.ui.viewmodel.BlockedAppsViewModel
import com.focusbubble.ui.viewmodel.FocusStatsViewModel

@Composable
fun DashboardScreen(
    userName: String?,
    navController: NavHostController,
    onMenuClick: () -> Unit = {},
    onStartFocusClick: (sessionMinutes: Int) -> Unit = {},
    onSchedulesClick: () -> Unit = {},
    onChatsClick: () -> Unit = {},
    onBlocksClick: () -> Unit = {}
) {

    var selectedTab by remember { mutableStateOf("Dashboard") }
    var showEditOptionsSheet by remember { mutableStateOf(false) }

    val blockedAppsViewModel: BlockedAppsViewModel = hiltViewModel()
    val focusStatsViewModel: FocusStatsViewModel = hiltViewModel()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.dashboard_bg),
            contentDescription = "Dashboard Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            TopBar(userName = userName ?: "User", onMenuClick = onMenuClick)
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ProfileFocusCard(
                    userName = userName,
                    focusStatsViewModel = focusStatsViewModel,
                    blockedAppsViewModel = blockedAppsViewModel,
                    onEditClick = { showEditOptionsSheet = true }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bubbly_icon),
                    contentDescription = "Mascot",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 12.dp)
                )

                // Start Focus Session Button (direct navigation)
                Button(
                    onClick = {
                        val duration = blockedAppsViewModel.selectedDurationMinutes.value
                        focusStatsViewModel.addFocusTime(duration)
                        navController.navigate("focusSession/$duration")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "Start Focus Session",
                        fontSize = 16.sp
                    )
                }
            }
        }

        if (showEditOptionsSheet) {
            EditOptionsSheet(
                viewModel = blockedAppsViewModel,
                onDismiss = { showEditOptionsSheet = false }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        "Schedules" -> onSchedulesClick()
                        "Chats" -> onChatsClick()
                        "Blocks" -> onBlocksClick()
                    }
                }
            )
        }
    }
}
