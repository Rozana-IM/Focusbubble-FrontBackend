package com.focusbubble.ui.viewmodel

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusbubble.data.entities.BlockedApp
import com.focusbubble.data.repository.BlockedAppsRepository
import com.focusbubble.ui.utils.UserAppInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ✅ Utility extensions to convert Drawable -> ImageBitmap
fun Drawable.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(
        intrinsicWidth.takeIf { it > 0 } ?: 1,
        intrinsicHeight.takeIf { it > 0 } ?: 1,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

fun Drawable.toImageBitmap(): ImageBitmap {
    return this.toBitmap().asImageBitmap()
}

@HiltViewModel
class BlockedAppsViewModel @Inject constructor(
    private val repository: BlockedAppsRepository
) : ViewModel() {

    // 🔹 Expose blocked apps directly from repository (database)
    val blockedApps: StateFlow<List<BlockedApp>> = repository.blockedApps
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 🔹 For displaying icons in the UI, map BlockedApp -> UserAppInfo
    private val _blockedAppsUi = MutableStateFlow<List<UserAppInfo>>(emptyList())
    val blockedAppsUi: StateFlow<List<UserAppInfo>> get() = _blockedAppsUi

    // --- Persisted selections ---
    private val _selectedDurationMinutes = MutableStateFlow(25) // default 25 mins
    val selectedDurationMinutes: StateFlow<Int> get() = _selectedDurationMinutes

    private val _selectedQuote = MutableStateFlow("Motivational")
    val selectedQuote: StateFlow<String> get() = _selectedQuote

    // --- Functions to update selections ---
    fun setSelectedDuration(minutes: Int) {
        _selectedDurationMinutes.value = minutes
    }

    fun setSelectedQuote(quote: String) {
        _selectedQuote.value = quote
    }

    // --- Existing blocked apps functions ---
    fun addApp(packageName: String, appName: String, duration: Int) {
        viewModelScope.launch {
            val app = BlockedApp(
                packageName = packageName,
                appName = appName,
                durationMinutes = duration
            )
            repository.addBlockedApp(app)
        }
    }

    fun deleteApp(app: BlockedApp) {
        viewModelScope.launch { repository.deleteBlockedApp(app) }
    }

    fun deleteAppByPackageName(packageName: String) {
        viewModelScope.launch {
            val toRemove = blockedApps.value.find { it.packageName == packageName }
            if (toRemove != null) repository.deleteBlockedApp(toRemove)
        }
    }

    fun updateBlockedApps(
        selectedPackages: Set<String>,
        allApps: List<UserAppInfo>
    ) {
        viewModelScope.launch {
            // Add new selections
            selectedPackages.forEach { pkg ->
                val appInfo = allApps.find { it.packageName == pkg }
                if (appInfo != null) {
                    addApp(pkg, appInfo.appName, 30) // default 30 mins
                }
            }
            // Remove apps not selected anymore
            val toRemove = blockedApps.value.filter { it.packageName !in selectedPackages }
            toRemove.forEach { deleteApp(it) }

            // ✅ Update UI list with ImageBitmap icons
            // ✅ Update UI list with ImageBitmap icons
            _blockedAppsUi.value = allApps
                .filter { it.packageName in selectedPackages }
                .map { app ->
                    app.copy(iconBitmap = app.iconBitmap)
                }

        }
    }
}
