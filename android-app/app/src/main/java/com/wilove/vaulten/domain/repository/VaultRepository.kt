package com.wilove.vaulten.domain.repository

import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.model.SecurityAlert
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing vault entries.
 */
interface VaultRepository {
    /**
     * Retrieves recent credentials as a reactive stream.
     */
    fun getRecentCredentials(limit: Int): Flow<List<Credential>>

    /**
     * Retrieves all credentials as a reactive stream.
     */
    fun getAllCredentials(): Flow<List<Credential>>

    /**
     * Retrieves security alerts.
     */
    suspend fun getSecurityAlerts(): List<SecurityAlert>

    /**
     * Retrieves a single credential by ID.
     */
    suspend fun getCredentialById(id: String): Credential?

    /**
     * Saves a credential (create or update).
     */
    suspend fun saveCredential(credential: Credential)

    /**
     * Soft-deletes a credential (moves it to trash). Does NOT call the API —
     * the server entry is only deleted when [permanentlyDeleteCredential] is called.
     */
    suspend fun deleteCredential(id: String)

    /**
     * Returns all credentials in the trash as a reactive stream.
     */
    fun getDeletedCredentials(): Flow<List<Credential>>

    /**
     * Restores a credential from trash (local only — the server entry was already deleted).
     * Note: the next full sync will remove this credential again since the server no longer has it.
     */
    suspend fun restoreCredential(id: String)

    /**
     * Permanently removes a credential from both the server and local database.
     * This is the only place the DELETE API is called.
     */
    suspend fun permanentlyDeleteCredential(id: String)

    /**
     * Synchronizes local data with the remote server.
     */
    suspend fun sync()
}
