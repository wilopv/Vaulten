package com.wilove.vaulten.data.repository

import com.wilove.vaulten.domain.model.AlertSeverity
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.model.SecurityAlert
import com.wilove.vaulten.domain.repository.VaultRepository
import kotlinx.coroutines.delay

/**
 * Fake implementation of [VaultRepository] for testing and development.
 * Provides mock data without requiring a real backend or database.
 *
 * This implementation simulates network delays and provides realistic test data.
 */
class FakeVaultRepository : VaultRepository {
    private val mockCredentials = mutableListOf(
        Credential(
            id = "1",
            name = "Gmail",
            username = "user@gmail.com",
            password = "encrypted_password_1",
            url = "https://gmail.com",
            lastModified = System.currentTimeMillis() - 3600000 // 1 hour ago
        ),
        Credential(
            id = "2",
            name = "GitHub",
            username = "developer",
            password = "encrypted_password_2",
            url = "https://github.com",
            lastModified = System.currentTimeMillis() - 7200000 // 2 hours ago
        ),
        Credential(
            id = "3",
            name = "Netflix",
            username = "viewer@email.com",
            password = "encrypted_password_3",
            url = "https://netflix.com",
            lastModified = System.currentTimeMillis() - 86400000 // 1 day ago
        ),
        Credential(
            id = "4",
            name = "Amazon",
            username = "shopper@email.com",
            password = "encrypted_password_4",
            url = "https://amazon.com",
            lastModified = System.currentTimeMillis() - 172800000 // 2 days ago
        ),
        Credential(
            id = "5",
            name = "Twitter",
            username = "@username",
            password = "encrypted_password_5",
            url = "https://twitter.com",
            lastModified = System.currentTimeMillis() - 259200000 // 3 days ago
        )
    )

    private val mockAlerts = listOf(
        SecurityAlert(
            id = "alert_1",
            message = "2 passwords are weak and should be updated",
            severity = AlertSeverity.WARNING
        ),
        SecurityAlert(
            id = "alert_2",
            message = "Last sync: 2 hours ago",
            severity = AlertSeverity.INFO
        )
    )

    override suspend fun getRecentCredentials(limit: Int): List<Credential> {
        delay(300) // Simulate network delay
        return mockCredentials
            .sortedByDescending { it.lastModified }
            .take(limit)
    }

    override suspend fun getSecurityAlerts(): List<SecurityAlert> {
        delay(200) // Simulate network delay
        return mockAlerts
    }

    override suspend fun getAllCredentials(): List<Credential> {
        delay(300) // Simulate network delay
        return mockCredentials.toList()
    }

    override suspend fun getCredentialById(id: String): Credential? {
        delay(200) // Simulate network delay
        return mockCredentials.find { it.id == id }
    }

    override suspend fun saveCredential(credential: Credential) {
        delay(300) // Simulate network delay
        val index = mockCredentials.indexOfFirst { it.id == credential.id }
        if (index >= 0) {
            mockCredentials[index] = credential
        } else {
            mockCredentials.add(credential)
        }
    }

    override suspend fun deleteCredential(id: String) {
        delay(300) // Simulate network delay
        mockCredentials.removeIf { it.id == id }
    }
}
