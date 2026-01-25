package com.wilove.vaulten.ui.signup

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SignupViewModelTest {

    @Test
    fun `onSignupClick returns false when email is blank`() {
        val viewModel = SignupViewModel()

        val result = viewModel.onSignupClick()

        assertFalse(result)
        assertEquals("Full name is required.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onSignupClick returns false when full name is blank`() {
        val viewModel = SignupViewModel()
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")

        val result = viewModel.onSignupClick()

        assertFalse(result)
        assertEquals("Full name is required.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onSignupClick returns false when email is invalid`() {
        val viewModel = SignupViewModel()
        viewModel.onFullNameChange("User Name")
        viewModel.onEmailChange("not-an-email")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")

        val result = viewModel.onSignupClick()

        assertFalse(result)
        assertEquals("Enter a valid email address.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onSignupClick returns false when password is blank`() {
        val viewModel = SignupViewModel()
        viewModel.onFullNameChange("User Name")
        viewModel.onEmailChange("user@example.com")

        val result = viewModel.onSignupClick()

        assertFalse(result)
        assertEquals("Master password is required.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onSignupClick returns false when confirm password is blank`() {
        val viewModel = SignupViewModel()
        viewModel.onFullNameChange("User Name")
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")

        val result = viewModel.onSignupClick()

        assertFalse(result)
        assertEquals("Confirm your password.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onSignupClick returns false when passwords do not match`() {
        val viewModel = SignupViewModel()
        viewModel.onFullNameChange("User Name")
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password456")

        val result = viewModel.onSignupClick()

        assertFalse(result)
        assertEquals("Passwords do not match.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onSignupClick returns true when inputs are valid`() {
        val viewModel = SignupViewModel()
        viewModel.onFullNameChange("User Name")
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")

        val result = viewModel.onSignupClick()

        assertTrue(result)
        assertTrue(viewModel.uiState.value.isLoading)
    }
}
