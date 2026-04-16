package com.wilove.vaulten.ui.signup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
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
@HiltViewModel
class SignupViewModel @Inject constructor(
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
            _uiState.update { it.copy(errorMessage = "El nombre es obligatorio.") }
            return
        }

        if (current.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El email es obligatorio.") }
            return
        }

        if (!EMAIL_REGEX.matches(current.email)) {
            _uiState.update { it.copy(errorMessage = "Introduce un email válido.") }
            return
        }

        if (current.masterPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "La contraseña es obligatoria.") }
            return
        }

        if (current.confirmPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Confirma tu contraseña.") }
            return
        }

        if (current.masterPassword != current.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        // Clear any previous session before starting a new registration
        authRepository.logout()

        viewModelScope.launch {
            // Using email as identifier for registration and login
            val registerResult = authRepository.register(current.fullName, current.email, current.masterPassword)
            
            if (registerResult.isSuccess) {
                // Auto-login after successful registration
                val loginResult = authRepository.login(current.email, current.masterPassword)
                
                _uiState.update { it.copy(isLoading = false) }
                
                loginResult.onSuccess {
                    onSuccess()
                }.onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message ?: "Cuenta creada, pero no se pudo iniciar sesión automáticamente. Inicia sesión manualmente.") }
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = registerResult.exceptionOrNull()?.message ?: "No se pudo crear la cuenta. Inténtalo de nuevo."
                    )
                }
            }
        }
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
