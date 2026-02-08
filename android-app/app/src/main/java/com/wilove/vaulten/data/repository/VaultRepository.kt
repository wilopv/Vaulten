package com.wilove.vaulten.data.repository

import com.wilove.vaulten.data.remote.VaultApiService
import com.wilove.vaulten.data.remote.model.*
import retrofit2.Response

/**
 * Repository responsible for managing vault entries and synchronization.
 */
class VaultRepository(
    private val apiService: VaultApiService
) {

    /**
     * Retrieves all vault entries.
     */
    suspend fun getEntries(): Result<List<VaultEntryResponse>> {
        return handleApiCall { apiService.getEntries() }
    }

    /**
     * Creates a new vault entry.
     */
    suspend fun createEntry(request: VaultEntryRequest): Result<VaultEntryResponse> {
        return handleApiCall { apiService.createEntry(request) }
    }

    /**
     * Gets a specific vault entry by ID.
     */
    suspend fun getEntry(id: Long): Result<VaultEntryResponse> {
        return handleApiCall { apiService.getEntry(id) }
    }

    /**
     * Updates an existing vault entry.
     */
    suspend fun updateEntry(id: Long, request: VaultEntryRequest): Result<VaultEntryResponse> {
        return handleApiCall { apiService.updateEntry(id, request) }
    }

    /**
     * Deletes a vault entry. Returns failure context if the deletion fails.
     */
    suspend fun deleteEntry(id: Long): Result<Unit> {
        return try {
            val response = apiService.deleteEntry(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error deleting entry: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Syncs modified entries since the given timestamp.
     */
    suspend fun sync(since: String): Result<SyncResponse> {
        return handleApiCall { apiService.sync(since) }
    }

    /**
     * Helper function to encapsulate API service calls and map them to [Result].
     */
    private suspend fun <T> handleApiCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
