package com.focusbubble.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.focusbubble.ui.BlockOverlayActivity
import com.focusbubble.data.api.RetrofitInstance
import com.focusbubble.data.entities.BlockedApp
import kotlinx.coroutines.*
import android.app.usage.UsageStatsManager

class BlockerService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var blockedApps: List<BlockedApp> = emptyList()
    private var currentBlockedApp: String? = null

    private var isPaused = false
    private var remainingTime: Long = 25 * 60 * 1000L // default 25 min
    private var timer: CountDownTimer? = null
    private val notificationId = 1
    private val channelId = "FocusBubbleBlocker"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        fetchBlockedApps()
        startForeground(notificationId, buildNotification())
        startTimer(remainingTime)
        Log.d("BlockerService", "Service started")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            "PAUSE_SESSION" -> togglePauseResume()
            "STOP_SESSION" -> stopSession()
        }

        serviceScope.launch {
            while (isActive) {
                if (!isPaused) {
                    checkForegroundApp()
                }
                delay(2000)
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        timer?.cancel()
        Log.d("BlockerService", "Service stopped")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // Timer & Notification
    private fun startTimer(timeMillis: Long) {
        timer?.cancel()
        if (isPaused) return

        timer = object : CountDownTimer(timeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                updateNotification()
            }

            override fun onFinish() {
                remainingTime = 0L
                updateNotification()
                stopForeground(true)
                stopSelf()
            }
        }.start()
    }

    private fun togglePauseResume() {
        isPaused = !isPaused
        if (!isPaused) startTimer(remainingTime)
        else timer?.cancel()
        updateNotification()
    }

    private fun stopSession() {
        timer?.cancel()
        stopForeground(true)
        stopSelf()
    }

    private fun buildNotification(): Notification {
        val pauseIntent = Intent(this, BlockerService::class.java).apply {
            action = "PAUSE_SESSION"
        }
        val pausePendingIntent = PendingIntent.getService(
            this, 0, pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, BlockerService::class.java).apply {
            action = "STOP_SESSION"
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mainIntent = Intent(this, BlockOverlayActivity::class.java)
        val mainPendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val minutes = (remainingTime / 1000) / 60
        val seconds = (remainingTime / 1000) % 60
        val timeText = String.format("%02d:%02d", minutes, seconds)
        val blockedCount = blockedApps.count { it.is_active }

        val pauseText = if (isPaused) "Resume" else "Pause"

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Focus Session")
            .setContentText("$timeText remaining • $blockedCount apps blocked")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(mainPendingIntent)
            .addAction(0, pauseText, pausePendingIntent)
            .addAction(0, "Stop", stopPendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, buildNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                channelId,
                "Focus Bubble Blocking Service",
                NotificationManager.IMPORTANCE_LOW
            )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(chan)
        }
    }

    // App Blocking
    private fun fetchBlockedApps() {
        serviceScope.launch {
            try {
                val response = RetrofitInstance.api.getBlockedApps()
                if (response.isSuccessful) {
                    blockedApps = response.body() ?: emptyList()
                    Log.d("BlockerService", "Blocked apps updated: ${blockedApps.size}")
                }
            } catch (e: Exception) {
                Log.e("BlockerService", "Error fetching blocked apps: ${e.message}")
            }
        }
    }

    private fun checkForegroundApp() {
        val usageStatsManager =
            getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val endTime = System.currentTimeMillis()
        val startTime = endTime - 2000

        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        if (usageStatsList.isNullOrEmpty()) return

        val recentApp = usageStatsList.maxByOrNull { it.lastTimeUsed }
        recentApp?.let { stats ->
            val foregroundPackage = stats.packageName
            val isBlocked = blockedApps.any { it.packageName == foregroundPackage && it.is_active }

            if (isBlocked && currentBlockedApp != foregroundPackage) {
                currentBlockedApp = foregroundPackage
                launchBlockOverlay(foregroundPackage)
            } else if (!isBlocked && currentBlockedApp == foregroundPackage) {
                currentBlockedApp = null
            }
        }
    }

    private fun launchBlockOverlay(packageName: String) {
        if (!Settings.canDrawOverlays(this)) return

        val overlayIntent = Intent(this, BlockOverlayActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("PACKAGE_NAME", packageName)
        }
        startActivity(overlayIntent)
    }
}
