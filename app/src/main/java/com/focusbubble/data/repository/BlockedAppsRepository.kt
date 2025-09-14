package com.focusbubble.data.repository

import com.focusbubble.data.api.RetrofitInstance
import com.focusbubble.data.dao.BlockedAppDao
import com.focusbubble.data.entities.BlockedApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BlockedAppsRepository(private val dao: BlockedAppDao) {

    val blockedApps: Flow<List<BlockedApp>> = dao.getAllBlockedApps()

    suspend fun addBlockedApp(app: BlockedApp) {
        // Save locally
        dao.insertBlockedApp(app)

        // Sync to backend
        withContext(Dispatchers.IO) {
            try {
                RetrofitInstance.api.addBlockedApp(app)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun deleteBlockedApp(app: BlockedApp) {
        dao.deleteBlockedApp(app)
        withContext(Dispatchers.IO) {
            try {
                RetrofitInstance.api.deleteBlockedApp(app.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun refreshFromBackend() {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getBlockedApps()
                if (response.isSuccessful) {
                    response.body()?.let { apps ->
                        apps.forEach { dao.insertBlockedApp(it) }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
