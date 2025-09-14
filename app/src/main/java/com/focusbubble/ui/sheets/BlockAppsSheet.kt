package com.focusbubble.ui.sheets

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusbubble.ui.utils.UserAppInfo
import com.focusbubble.ui.viewmodel.BlockedAppsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockAppsSheet(
    viewModel: BlockedAppsViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val pm = context.packageManager

    // Fetch installed apps
    var allApps by remember { mutableStateOf<List<UserAppInfo>>(emptyList()) }
    var selectedPackages by remember { mutableStateOf(viewModel.blockedApps.value.map { it.packageName }.toSet()) }

    LaunchedEffect(Unit) {
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        //.filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 } // uncomment to show user apps only
        allApps = apps.map {
            val drawable = try { pm.getApplicationIcon(it.packageName) } catch (_: Exception) { pm.getDefaultActivityIcon() }
            UserAppInfo(
                appName = it.loadLabel(pm).toString(),
                packageName = it.packageName,
                iconBitmap = drawableToImageBitmap(drawable) // ✅ match new field
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1C1C1C),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Select Apps to Block", fontSize = 20.sp, color = Color.White)
            Spacer(Modifier.height(12.dp))

            LazyColumn {
                items(allApps) { app ->
                    val isSelected = selectedPackages.contains(app.packageName)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF2C2C2C), androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            bitmap = app.iconBitmap,
                            contentDescription = app.appName,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(app.appName, color = Color.White, modifier = Modifier.weight(1f))

                        Switch(
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                // ✅ Request overlay permission only if needed
                                if (!Settings.canDrawOverlays(context)) {
                                    val intent = Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:${context.packageName}")
                                    )
                                    context.startActivity(intent)
                                } else {
                                    selectedPackages = if (checked) {
                                        selectedPackages + app.packageName
                                    } else {
                                        selectedPackages - app.packageName
                                    }
                                }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF3D8DFF))
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.updateBlockedApps(selectedPackages, allApps)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
            ) {
                Text("Confirm", fontSize = 16.sp)
            }
        }
    }
}

// Convert Drawable → ImageBitmap
private fun drawableToImageBitmap(drawable: Drawable): androidx.compose.ui.graphics.ImageBitmap {
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth.takeIf { it > 0 } ?: 1,
        drawable.intrinsicHeight.takeIf { it > 0 } ?: 1,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap.asImageBitmap()
}
