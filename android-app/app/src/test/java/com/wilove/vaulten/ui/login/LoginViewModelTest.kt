package com.wilove.vaulten.ui.login

import com.wilove.vaulten.data.local.TokenManager
import com.wilove.vaulten.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var tokenManager: TokenManager
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        tokenManager = mockk(relaxed = true)
        viewModel = LoginViewModel(authRepository, tokenManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onUnlockClick shows error when email is blank`() {
        viewModel.onEmailChange("")
        var successCalled = false
        
        viewModel.onUnlockClick { successCalled = true }

        assertFalse(successCalled)
        assertEquals("El email es obligatorio.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onUnlockClick shows error when email is invalid`() {
        viewModel.onEmailChange("not-an-email")
        var successCalled = false

        viewModel.onUnlockClick { successCalled = true }

        assertFalse(successCalled)
        assertEquals("Introduce un email válido.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onUnlockClick shows error when password is blank`() {
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("")
        var successCalled = false

        viewModel.onUnlockClick { successCalled = true }

        assertFalse(successCalled)
        assertEquals("La contraseña es obligatoria.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onUnlockClick calls repository login when data is valid`() = runTest(testDispatcher) {
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")
        coEvery { authRepository.login(any(), any()) } returns Result.success("token")
        
        var successCalled = false
        viewModel.onUnlockClick { successCalled = true }
        
        assertTrue(viewModel.uiState.value.isLoading)
        advanceUntilIdle()

        assertTrue(successCalled)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
