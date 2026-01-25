package com.wilove.vaulten.ui.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Login screen.
 * Owns [LoginUiState] and exposes intent handlers for the UI.
 */
class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /** Updates the password field as the user types. */
    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(masterPassword = value, errorMessage = null) }
    }

    /** Updates the email field as the user types. */
    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    /** Enables or disables biometric unlock in the UI. */
    fun onBiometricToggle(enabled: Boolean) {
        _uiState.update { it.copy(biometricEnabled = enabled) }
    }

    /**
     * Mock-only action for the primary button.
     * Validates empty input and toggles a loading state without real auth.
     */
    fun onUnlockClick(): Boolean {
        val current = _uiState.value
        if (current.isLockedOut || current.isLoading) return false

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

        // Mock-only: show a loading state without real authentication.
        // TODO: Wire email + password into the real auth use case once available.
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        return true
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
