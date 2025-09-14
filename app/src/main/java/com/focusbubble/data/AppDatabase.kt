package com.focusbubble.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.focusbubble.data.dao.BlockedAppDao
import com.focusbubble.data.entities.BlockedApp

@Database(entities = [BlockedApp::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blockedAppDao(): BlockedAppDao
}
