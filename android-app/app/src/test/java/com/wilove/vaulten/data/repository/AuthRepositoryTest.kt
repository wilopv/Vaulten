package com.wilove.vaulten.data.repository

import com.wilove.vaulten.data.remote.AuthApiService
import com.wilove.vaulten.data.remote.model.AuthResponse
import com.wilove.vaulten.data.remote.model.LoginRequest
import com.wilove.vaulten.data.local.TokenManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class AuthRepositoryTest {

    private lateinit var authApiService: AuthApiService
    private lateinit var tokenManager: TokenManager
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        authApiService = mockk()
        tokenManager = mockk(relaxed = true)
        repository = AuthRepositoryImpl(authApiService, tokenManager)
    }

    @Test
    fun `login success saves token and returns result`() = runTest {
        val username = "user"
        val password = "pass"
        val token = "jwt-token"
        coEvery { authApiService.login(any()) } returns Response.success(AuthResponse(token))

        val result = repository.login(username, password)

        assertTrue(result.isSuccess)
        assertEquals(token, result.getOrNull())
        verify { tokenManager.saveToken(token) }
    }

    @Test
    fun `login failure returns failure result`() = runTest {
        val username = "user"
        val password = "pass"
        coEvery { authApiService.login(any()) } returns Response.error(401, mockk(relaxed = true))

        val result = repository.login(username, password)

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { tokenManager.saveToken(any()) }
    }

    @Test
    fun `logout deletes token`() {
        repository.logout()
        verify { tokenManager.deleteToken() }
    }
}
