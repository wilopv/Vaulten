package com.wilove.vaulten.domain.usecase

import com.wilove.vaulten.domain.model.AlertSeverity
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.model.SecurityAlert
import com.wilove.vaulten.domain.repository.VaultRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [GetDashboardDataUseCase].
 * Verifies that the use case correctly aggregates data from the repository.
 */
class GetDashboardDataUseCaseTest {

    private lateinit var repository: VaultRepository
    private lateinit var useCase: GetDashboardDataUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetDashboardDataUseCase(repository)
    }

    @Test
    fun `invoke returns dashboard data with recent credentials and alerts`() = runTest {
        // Given
        val mockCredentials = listOf(
            Credential(
                id = "1",
                name = "Gmail",
                username = "user@gmail.com",
                password = "encrypted",
                url = "https://gmail.com"
            ),
            Credential(
                id = "2",
                name = "GitHub",
                username = "dev",
                password = "encrypted",
                url = "https://github.com"
            )
        )
        val mockAlerts = listOf(
            SecurityAlert(
                id = "alert1",
                message = "Weak password detected",
                severity = AlertSeverity.WARNING
            )
        )
        val allCredentials = mockCredentials + listOf(
            Credential("3", "Netflix", "user", "pass", "https://netflix.com")
        )

        coEvery { repository.getRecentCredentials(5) } returns mockCredentials
        coEvery { repository.getSecurityAlerts() } returns mockAlerts
        coEvery { repository.getAllCredentials() } returns allCredentials

        // When
        val result = useCase()

        // Then
        assertEquals(mockCredentials, result.recentCredentials)
        assertEquals(mockAlerts, result.securityAlerts)
        assertEquals(3, result.totalCredentials)

        coVerify { repository.getRecentCredentials(5) }
        coVerify { repository.getSecurityAlerts() }
        coVerify { repository.getAllCredentials() }
    }

    @Test
    fun `invoke returns empty dashboard data when repository is empty`() = runTest {
        // Given
        coEvery { repository.getRecentCredentials(5) } returns emptyList()
        coEvery { repository.getSecurityAlerts() } returns emptyList()
        coEvery { repository.getAllCredentials() } returns emptyList()

        // When
        val result = useCase()

        // Then
        assertEquals(emptyList<Credential>(), result.recentCredentials)
        assertEquals(emptyList<SecurityAlert>(), result.securityAlerts)
        assertEquals(0, result.totalCredentials)
    }

    @Test
    fun `invoke limits recent credentials to 5`() = runTest {
        // Given
        val mockCredentials = (1..3).map {
            Credential(
                id = "$it",
                name = "Service $it",
                username = "user$it",
                password = "pass",
                url = null
            )
        }

        coEvery { repository.getRecentCredentials(5) } returns mockCredentials
        coEvery { repository.getSecurityAlerts() } returns emptyList()
        coEvery { repository.getAllCredentials() } returns mockCredentials

        // When
        val result = useCase()

        // Then
        assertEquals(3, result.recentCredentials.size)
        coVerify { repository.getRecentCredentials(5) }
    }
}
