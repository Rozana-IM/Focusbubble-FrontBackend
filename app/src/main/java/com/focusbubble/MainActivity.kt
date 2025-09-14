package com.focusbubble

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.focusbubble.service.BlockerService
import com.focusbubble.ui.screens.*
import com.focusbubble.ui.theme.FocusBubbleTheme
import com.focusbubble.ui.viewmodel.BlockedAppsViewModel
import com.focusbubble.ui.viewmodel.FocusStatsViewModel
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val REQUEST_NOTIFICATION_PERMISSION = 1001
    private var startServiceAfterPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)

        val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)

        setContent {
            FocusBubbleTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost(sharedPrefs) { startFocusServiceIfPermissionGranted() }
                }
            }
        }
    }

    private fun startFocusServiceIfPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                startServiceAfterPermission = true
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            } else {
                startFocusService()
            }
        } else {
            startFocusService()
        }
    }

    private fun startFocusService() {
        val serviceIntent = Intent(this, BlockerService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (startServiceAfterPermission) {
                    startFocusService()
                    startServiceAfterPermission = false
                }
            } else {
                Toast.makeText(
                    this,
                    "Notification permission is required to start focus session",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

@Composable
fun AppNavHost(
    sharedPrefs: android.content.SharedPreferences,
    onStartFocusClick: () -> Unit
) {
    val navController = rememberNavController()
    val blockedAppsViewModel: BlockedAppsViewModel = hiltViewModel()
    val focusStatsViewModel: FocusStatsViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "welcome") {

        composable("welcome") {
            WelcomeScreen(onContinue = { name ->
                if (name.isNotBlank()) {
                    sharedPrefs.edit().putString("profile_name", name).apply()
                }
                navController.navigate("intro")
            })
        }

        composable("intro") {
            BubblyIntroScreen(onContinue = {
                navController.navigate("dashboard") {
                    popUpTo("welcome") { inclusive = true }
                }
            })
        }

        composable("dashboard") {
            val profileName = sharedPrefs.getString("profile_name", "User") ?: "User"

            DashboardScreen(
                userName = profileName,
                navController = navController,
                onStartFocusClick = { duration ->
                    focusStatsViewModel.addFocusTime(duration)
                    onStartFocusClick() // Start service if permission granted
                    navController.navigate("focusSession/$duration") // Navigate to FocusSessionScreen
                },
                onSchedulesClick = { navController.navigate("schedules") },
                onChatsClick = { navController.navigate("chats") },
                onBlocksClick = { navController.navigate("blocks") }
            )
        }

        composable("schedules") {
            SchedulesScreen(onBackClick = { navController.popBackStack() })
        }

        composable("chats") {
            ChatsScreen(onBackClick = { navController.popBackStack() })
        }

        composable("blocks") {
            BlocksScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = blockedAppsViewModel
            )
        }

        // FocusSessionScreen route
        composable("focusSession/{duration}") { backStackEntry ->
            val duration = backStackEntry.arguments?.getString("duration")?.toInt() ?: 25
            FocusSessionScreen(
                durationMinutes = duration,
                onStop = { navController.popBackStack() },
                onFinish = { navController.popBackStack() },
                onFocusSessionsClick = {
                    // Clickable "Focus Sessions" text
                    navController.navigate("dashboard")
                }
            )
        }
    }
}
