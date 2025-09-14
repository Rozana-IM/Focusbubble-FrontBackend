package com.focusbubble.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusbubble.data.entities.BlockedApp
import com.focusbubble.ui.viewmodel.BlockedAppsViewModel

@Composable
fun BlocksScreen(
    onBackClick: () -> Unit,
    viewModel: BlockedAppsViewModel = hiltViewModel()
) {
    // blockedApps is List<BlockedApp> (database entities)
    val blockedApps by viewModel.blockedApps.collectAsState()

    ScreenWithBack(title = "Blocked Apps", onBackClick = onBackClick) { modifier ->
        Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
            if (blockedApps.isEmpty()) {
                Text("No blocked apps", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn {
                    items(blockedApps) { app: BlockedApp ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // show app name and duration stored in DB
                                Text("${app.appName} (${app.durationMinutes} mins)")
                                IconButton(
                                    onClick = { viewModel.deleteApp(app) } // delete expects BlockedApp
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
