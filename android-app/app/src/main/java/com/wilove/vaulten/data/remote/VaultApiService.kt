package com.wilove.vaulten.data.remote

import com.wilove.vaulten.data.remote.model.SyncResponse
import com.wilove.vaulten.data.remote.model.VaultEntryRequest
import com.wilove.vaulten.data.remote.model.VaultEntryResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit service for Vault management endpoints.
 */
interface VaultApiService {
    /**
     * Fetches all entries for the authenticated user.
     */
    @GET("api/vault")
    suspend fun getEntries(): Response<List<VaultEntryResponse>>

    /**
     * Creates a new vault entry.
     */
    @POST("api/vault")
    suspend fun createEntry(@Body request: VaultEntryRequest): Response<VaultEntryResponse>

    /**
     * Retrieves a single vault entry by its ID.
     */
    @GET("api/vault/{id}")
    suspend fun getEntry(@Path("id") id: Long): Response<VaultEntryResponse>

    /**
     * Updates an existing vault entry.
     */
    @PUT("api/vault/{id}")
    suspend fun updateEntry(@Path("id") id: Long, @Body request: VaultEntryRequest): Response<VaultEntryResponse>

    /**
     * Deletes a vault entry by its ID.
     */
    @DELETE("api/vault/{id}")
    suspend fun deleteEntry(@Path("id") id: Long): Response<Unit>

    /**
     * Performs an incremental synchronization using a timestamp.
     */
    @GET("api/vault/sync")
    suspend fun sync(@Query("since") since: String): Response<SyncResponse>
}
