package com.wilove.vaulten.ui.signup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Signup screen.
 * Owns [SignupUiState] and exposes intent handlers for the UI.
 */
class SignupViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    /** Updates the email field as the user types. */
    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    /** Updates the full name field as the user types. */
    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, errorMessage = null) }
    }

    /** Updates the password field as the user types. */
    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(masterPassword = value, errorMessage = null) }
    }

    /** Updates the confirm password field as the user types. */
    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    }

    /**
     * Mock-only action for the primary button.
     * Validates input and toggles a loading state without real auth.
     */
    fun onSignupClick(): Boolean {
        val current = _uiState.value
        if (current.isLoading) return false

        if (current.fullName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Full name is required.") }
            return false
        }

        if (current.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email is required.") }
            return false
        }

        if (!EMAIL_REGEX.matches(current.email)) {
            _uiState.update { it.copy(errorMessage = "Enter a valid email address.") }
            return false
        }

        if (current.masterPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Master password is required.") }
            return false
        }

        if (current.confirmPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Confirm your password.") }
            return false
        }

        if (current.masterPassword != current.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match.") }
            return false
        }

        // Mock-only: show a loading state without real authentication.
        // TODO: Wire email + password into the real signup flow once available.
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        return true
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
