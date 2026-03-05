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
    fun `onSignupClick calls repository register when inputs are valid`() = runTest(testDispatcher) {
        viewModel.onFullNameChange("User Name")
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")
        coEvery { authRepository.register(any(), any(), any()) } returns Result.success(Unit)
        
        var successCalled = false
        viewModel.onSignupClick { successCalled = true }
        
        assertTrue(viewModel.uiState.value.isLoading)
        advanceUntilIdle()

        assertTrue(successCalled)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
