package com.wilove.vaulten.domain.usecase

import com.wilove.vaulten.domain.model.AlertSeverity
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.model.SecurityAlert
import com.wilove.vaulten.domain.repository.VaultRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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
        val allCredentials = mockCredentials + listOf(
            Credential("3", "Netflix", "user", "pass", "https://netflix.com")
        )

        coEvery { repository.getRecentCredentials(5) } returns flowOf(mockCredentials)
        coEvery { repository.getAllCredentials() } returns flowOf(allCredentials)
        coEvery { repository.getSecurityAlerts() } returns emptyList()

        // When
        val result = useCase().first()

        // Then
        assertEquals(mockCredentials, result.recentCredentials)
        assertEquals(0, result.securityAlerts.size)
        assertEquals(3, result.totalCredentials)

        coVerify { repository.getRecentCredentials(5) }
        coVerify { repository.getAllCredentials() }
    }

    @Test
    fun `invoke returns empty dashboard data when repository is empty`() = runTest {
        // Given
        coEvery { repository.getRecentCredentials(5) } returns flowOf(emptyList())
        coEvery { repository.getAllCredentials() } returns flowOf(emptyList())
        coEvery { repository.getSecurityAlerts() } returns emptyList()

        // When
        val result = useCase().first()

        // Then
        assertEquals(emptyList<Credential>(), result.recentCredentials)
        assertEquals(0, result.totalCredentials)
    }
}
