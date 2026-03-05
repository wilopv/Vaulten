package com.wilove.vaulten.domain.usecase

import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.repository.VaultRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAllCredentialsUseCaseTest {

    @Test
    fun `invoke returns all credentials from repository`() = runTest {
        val repository = mockk<VaultRepository>()
        val credentials = listOf(
            Credential("1", "Gmail", "user@gmail.com", "pass", "https://gmail.com"),
            Credential("2", "GitHub", "dev", "pass", "https://github.com")
        )
        coEvery { repository.getAllCredentials() } returns flowOf(credentials)

        val useCase = GetAllCredentialsUseCase(repository)
        val result = useCase().first()

        assertEquals(credentials, result)
        coVerify(exactly = 1) { repository.getAllCredentials() }
    }
}
