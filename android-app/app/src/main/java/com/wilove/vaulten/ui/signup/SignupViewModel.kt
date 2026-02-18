package com.wilove.vaulten.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wilove.vaulten.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Signup screen.
 * Owns [SignupUiState] and exposes intent handlers for the UI.
 */
class SignupViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
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
     * Registers the user using the provided information.
     */
    fun onSignupClick(onSuccess: () -> Unit) {
        val current = _uiState.value
        if (current.isLoading) return

        if (current.fullName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Full name is required.") }
            return
        }

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

        if (current.confirmPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Confirm your password.") }
            return
        }

        if (current.masterPassword != current.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            // Using fullName as username for the backend RegisterRequest
            val result = authRepository.register(current.fullName, current.email, current.masterPassword)
            
            _uiState.update { it.copy(isLoading = false) }
            
            result.onSuccess {
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "Registration failed") }
            }
        }
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
