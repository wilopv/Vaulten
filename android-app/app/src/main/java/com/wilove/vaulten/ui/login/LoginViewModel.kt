package com.wilove.vaulten.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wilove.vaulten.data.local.TokenManager
import com.wilove.vaulten.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LoginUiState(hasBiometricCredentials = tokenManager.hasBiometricCredentials())
    )

    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(masterPassword = value, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onUnlockClick(onSuccess: () -> Unit) {
        val current = _uiState.value
        if (current.isLockedOut || current.isLoading) return

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

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = authRepository.login(current.email, current.masterPassword)
            _uiState.update { it.copy(isLoading = false) }
            result.onSuccess {
                tokenManager.saveBiometricCredentials(current.email, current.masterPassword)
                _uiState.update { it.copy(hasBiometricCredentials = true) }
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "No se pudo iniciar sesión. Inténtalo de nuevo.") }
            }
        }
    }

    /**
     * Called by the screen after a successful biometric prompt on the login screen.
     * Reads the saved credentials and logs in silently.
     */
    fun onBiometricLoginSuccess(onSuccess: () -> Unit) {
        val email = tokenManager.getBiometricEmail() ?: return
        val password = tokenManager.getBiometricPassword() ?: return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = authRepository.login(email, password)
            _uiState.update { it.copy(isLoading = false) }
            result.onSuccess {
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "No se pudo iniciar sesión. Inténtalo de nuevo.") }
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.update { LoginUiState(hasBiometricCredentials = tokenManager.hasBiometricCredentials()) }
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
