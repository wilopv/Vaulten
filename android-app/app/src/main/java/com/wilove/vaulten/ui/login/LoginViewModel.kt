package com.wilove.vaulten.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wilove.vaulten.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Login screen.
 * Owns [LoginUiState] and exposes intent handlers for the UI.
 */
class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
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
     * Authenticates the user using the provided credentials.
     */
    fun onUnlockClick(onSuccess: () -> Unit) {
        val current = _uiState.value
        if (current.isLockedOut || current.isLoading) return

        if (current.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email is required.") }
            return
        }

        if (!EMAIL_REGEX.matches(current.email)) {
            _uiState.update { it.copy(errorMessage = "Enter a valid email address.") }
            return
        }

        if (current.masterPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Master password is required.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = authRepository.login(current.email, current.masterPassword)
            
            _uiState.update { it.copy(isLoading = false) }
            
            result.onSuccess {
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "Authentication failed") }
            }
        }
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
