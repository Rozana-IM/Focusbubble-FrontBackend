package com.focusbubble.data.api

import com.focusbubble.data.entities.BlockedApp
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("/blocked_apps")
    suspend fun getBlockedApps(): Response<List<BlockedApp>>

    @POST("/blocked_apps")
    suspend fun addBlockedApp(@Body app: BlockedApp): Response<BlockedApp>

    @DELETE("/blocked_apps/{id}")
    suspend fun deleteBlockedApp(@Path("id") id: Int): Response<Unit>
}
