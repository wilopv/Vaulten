package com.wilove.vaulten.ui.signup

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
class SignupViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: SignupViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        viewModel = SignupViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSignupClick shows error when full name is blank`() {
        viewModel.onFullNameChange("")
        var successCalled = false
        
        viewModel.onSignupClick { successCalled = true }

        assertFalse(successCalled)
        assertEquals("Full name is required.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onSignupClick shows error when email is blank`() {
        viewModel.onFullNameChange("User Name")
        viewModel.onEmailChange("")
        var successCalled = false

        viewModel.onSignupClick { successCalled = true }

        assertFalse(successCalled)
        assertEquals("Email is required.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onSignupClick shows error when email is invalid`() {
        viewModel.onFullNameChange("User Name")
        viewModel.onEmailChange("not-an-email")
        var successCalled = false

        viewModel.onSignupClick { successCalled = true }

        assertFalse(successCalled)
        assertEquals("Enter a valid email address.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onSignupClick shows error when passwords do not match`() {
        viewModel.onFullNameChange("User Name")
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password456")
        var successCalled = false

        viewModel.onSignupClick { successCalled = true }

        assertFalse(successCalled)
        assertEquals("Passwords do not match.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onSignupClick calls repository register and login when inputs are valid`() = runTest(testDispatcher) {
        viewModel.onFullNameChange("User Name")
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")
        
        coEvery { authRepository.logout() } returns Unit
        coEvery { authRepository.register(any(), any(), any()) } returns Result.success(Unit)
        coEvery { authRepository.login(any(), any()) } returns Result.success("mock-token")
        
        var successCalled = false
        viewModel.onSignupClick { successCalled = true }
        
        assertTrue(viewModel.uiState.value.isLoading)
        advanceUntilIdle()

        io.mockk.verify { authRepository.logout() }
        io.mockk.verify { authRepository.register("User Name", "user@example.com", "password123") }
        io.mockk.verify { authRepository.login("user@example.com", "password123") }
        
        assertTrue(successCalled)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onSignupClick shows error when register succeeds but login fails`() = runTest(testDispatcher) {
        viewModel.onFullNameChange("User Name")
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")
        
        coEvery { authRepository.logout() } returns Unit
        coEvery { authRepository.register(any(), any(), any()) } returns Result.success(Unit)
        coEvery { authRepository.login(any(), any()) } returns Result.failure(Exception("Login service unavailable"))
        
        var successCalled = false
        viewModel.onSignupClick { successCalled = true }
        
        advanceUntilIdle()

        assertFalse(successCalled)
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("automatic login failed"))
    }
}
