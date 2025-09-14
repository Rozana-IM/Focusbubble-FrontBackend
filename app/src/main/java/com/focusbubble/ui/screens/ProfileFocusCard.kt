package com.focusbubble.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusbubble.ui.viewmodel.BlockedAppsViewModel
import com.focusbubble.ui.viewmodel.FocusStatsViewModel

@Composable
fun ProfileFocusCard(
    userName: String?,
    focusStatsViewModel: FocusStatsViewModel,
    blockedAppsViewModel: BlockedAppsViewModel,
    onEditClick: () -> Unit
) {
    val name = userName ?: "User"
    val firstLetter = name.first().uppercaseChar()

    // ✅ collect state properly
    val weeklyTime by focusStatsViewModel.weeklyFocusTime.collectAsState()
    val blockedAppsUi by blockedAppsViewModel.blockedAppsUi.collectAsState()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(480.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Circle
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .border(3.dp, Color.White, CircleShape)
                        .background(Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = firstLetter.toString(),
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Weekly Focus Time
                Text(
                    text = String.format("%02d:%02d", weeklyTime / 60, weeklyTime % 60),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = "Time Focused",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Blocked Apps UI
                if (blockedAppsUi.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        blockedAppsUi.take(3).forEach { app ->
                            if (app.iconBitmap != null) {
                                Image(
                                    bitmap = app.iconBitmap,
                                    contentDescription = null, // ✅ No extra text in UI
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }
                        }

                        Text(
                            text = "${blockedAppsUi.size} Apps Blocked",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = "No Apps Blocked",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Edit Button
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.wrapContentWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit")
                }
            }
        }
    }
}
