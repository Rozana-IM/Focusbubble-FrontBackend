package com.focusbubble.ui.utils

import androidx.compose.ui.graphics.ImageBitmap

data class UserAppInfo(
    val packageName: String,
    val appName: String,
    val iconBitmap: ImageBitmap // always ImageBitmap
)
