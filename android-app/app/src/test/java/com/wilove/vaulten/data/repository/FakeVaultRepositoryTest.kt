package com.wilove.vaulten.data.repository

import com.wilove.vaulten.domain.model.AlertSeverity
import com.wilove.vaulten.domain.model.Credential
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [FakeVaultRepository].
 * Verifies that the fake repository provides consistent mock data.
 */
class FakeVaultRepositoryTest {

    private lateinit var repository: FakeVaultRepository

    @Before
    fun setup() {
        repository = FakeVaultRepository()
    }

    @Test
    fun `getRecentCredentials returns limited credentials sorted by lastModified`() = runTest {
        // When
        val result = repository.getRecentCredentials(limit = 3)

        // Then
        assertEquals(3, result.size)
        // Verify they are sorted by lastModified (descending)
        for (i in 0 until result.size - 1) {
            assertTrue(result[i].lastModified >= result[i + 1].lastModified)
        }
    }

    @Test
    fun `getRecentCredentials respects limit parameter`() = runTest {
        // When
        val result1 = repository.getRecentCredentials(limit = 2)
        val result2 = repository.getRecentCredentials(limit = 10)

        // Then
        assertEquals(2, result1.size)
        assertEquals(5, result2.size) // Repository has 5 mock credentials
    }

    @Test
    fun `getSecurityAlerts returns mock alerts`() = runTest {
        // When
        val result = repository.getSecurityAlerts()

        // Then
        assertEquals(2, result.size)
        assertTrue(result.any { it.severity == AlertSeverity.WARNING })
        assertTrue(result.any { it.severity == AlertSeverity.INFO })
    }

    @Test
    fun `getAllCredentials returns all mock credentials`() = runTest {
        // When
        val result = repository.getAllCredentials()

        // Then
        assertEquals(5, result.size)
        assertTrue(result.any { it.name == "Gmail" })
        assertTrue(result.any { it.name == "GitHub" })
        assertTrue(result.any { it.name == "Netflix" })
    }

    @Test
    fun `getCredentialById returns correct credential`() = runTest {
        // When
        val result = repository.getCredentialById("1")

        // Then
        assertNotNull(result)
        assertEquals("1", result?.id)
        assertEquals("Gmail", result?.name)
    }

    @Test
    fun `getCredentialById returns null for non-existent id`() = runTest {
        // When
        val result = repository.getCredentialById("non_existent")

        // Then
        assertNull(result)
    }

    @Test
    fun `saveCredential adds new credential`() = runTest {
        // Given
        val newCredential = Credential(
            id = "new_id",
            name = "New Service",
            username = "newuser",
            password = "newpass",
            url = "https://new.com"
        )

        // When
        repository.saveCredential(newCredential)
        val result = repository.getCredentialById("new_id")

        // Then
        assertNotNull(result)
        assertEquals("New Service", result?.name)
    }

    @Test
    fun `saveCredential updates existing credential`() = runTest {
        // Given
        val updatedCredential = Credential(
            id = "1",
            name = "Gmail Updated",
            username = "updated@gmail.com",
            password = "newpass",
            url = "https://gmail.com"
        )

        // When
        repository.saveCredential(updatedCredential)
        val result = repository.getCredentialById("1")

        // Then
        assertNotNull(result)
        assertEquals("Gmail Updated", result?.name)
        assertEquals("updated@gmail.com", result?.username)
    }

    @Test
    fun `deleteCredential removes credential`() = runTest {
        // Given
        val initialCount = repository.getAllCredentials().size

        // When
        repository.deleteCredential("1")
        val result = repository.getCredentialById("1")
        val finalCount = repository.getAllCredentials().size

        // Then
        assertNull(result)
        assertEquals(initialCount - 1, finalCount)
    }

    @Test
    fun `deleteCredential with non-existent id does not throw`() = runTest {
        // Given
        val initialCount = repository.getAllCredentials().size

        // When
        repository.deleteCredential("non_existent")
        val finalCount = repository.getAllCredentials().size

        // Then
        assertEquals(initialCount, finalCount)
    }
}
