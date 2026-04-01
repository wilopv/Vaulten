package com.wilove.vaulten.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wilove.vaulten.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val errorMessage: String? = null
)

class ChangePasswordViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun onCurrentPasswordChange(value: String) {
        _uiState.update { it.copy(currentPassword = value, currentPasswordError = null) }
    }

    fun onNewPasswordChange(value: String) {
        _uiState.update { it.copy(newPassword = value, newPasswordError = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun changePassword() {
        val state = _uiState.value

        val currentPasswordError = if (state.currentPassword.isBlank())
            "La contraseña actual es obligatoria." else null
        val newPasswordError = if (state.newPassword.length < 8)
            "La nueva contraseña debe tener al menos 8 caracteres." else null
        val confirmPasswordError = if (state.newPassword != state.confirmPassword)
            "Las contraseñas no coinciden." else null

        if (currentPasswordError != null || newPasswordError != null || confirmPasswordError != null) {
            _uiState.update {
                it.copy(
                    currentPasswordError = currentPasswordError,
                    newPasswordError = newPasswordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.changePassword(state.currentPassword, state.newPassword)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, savedSuccessfully = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message
                            ?: "Error al cambiar la contraseña"
                    )
                }
            }
        }
    }
}
