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
     * Deletes a credential.
     */
    suspend fun deleteCredential(id: String)

    /**
     * Synchronizes local data with the remote server.
     */
    suspend fun sync()
}
