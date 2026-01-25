package com.wilove.vaulten.ui.login

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginViewModelTest {

    @Test
    fun `onUnlockClick returns false when email is blank`() {
        val viewModel = LoginViewModel()

        val result = viewModel.onUnlockClick()

        assertFalse(result)
        assertEquals("Email is required.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onUnlockClick returns false when email is invalid`() {
        val viewModel = LoginViewModel()
        viewModel.onEmailChange("not-an-email")
        viewModel.onPasswordChange("password123")

        val result = viewModel.onUnlockClick()

        assertFalse(result)
        assertEquals("Enter a valid email address.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onUnlockClick returns false when password is blank`() {
        val viewModel = LoginViewModel()
        viewModel.onEmailChange("user@example.com")

        val result = viewModel.onUnlockClick()

        assertFalse(result)
        assertEquals("Master password is required.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onUnlockClick returns true when email and password are valid`() {
        val viewModel = LoginViewModel()
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")

        val result = viewModel.onUnlockClick()

        assertTrue(result)
        assertTrue(viewModel.uiState.value.isLoading)
    }
}
