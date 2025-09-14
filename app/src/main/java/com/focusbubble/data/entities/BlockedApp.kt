package com.focusbubble.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "blocked_apps")
data class BlockedApp(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageName: String,      // must match the ViewModel parameter
    val appName: String,
    val durationMinutes: Int,
    val is_active: Boolean = true
)