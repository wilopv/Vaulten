package com.wilove.vaulten.ui.passwordgenerator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.wilove.vaulten.domain.usecase.GeneratePasswordUseCase

/**
 * ViewModel for the Password Generator screen.
 * Manages password generation and configuration.
 */
class PasswordGeneratorViewModel(
    private val generatePasswordUseCase: GeneratePasswordUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PasswordGeneratorUiState())
    val uiState: StateFlow<PasswordGeneratorUiState> = _uiState.asStateFlow()

    init {
        generatePassword()
    }

    /**
     * Generates a new password based on current configuration.
     */
    fun generatePassword() {
        val state = _uiState.value
        try {
            val password = generatePasswordUseCase(
                length = state.length,
                useUppercase = state.useUppercase,
                useLowercase = state.useLowercase,
                useNumbers = state.useNumbers,
                useSymbols = state.useSymbols
            )
            _uiState.update { it.copy(generatedPassword = password, errorMessage = null) }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = e.message ?: "Error generating password") }
        }
    }

    /**
     * Updates the password length.
     */
    fun setLength(length: Int) {
        _uiState.update { it.copy(length = length) }
        generatePassword()
    }

    /**
     * Toggles uppercase characters.
     */
    fun toggleUppercase() {
        val newState = _uiState.value.useUppercase.not()
        if (!newState && !_uiState.value.useLowercase && !_uiState.value.useNumbers && !_uiState.value.useSymbols) {
            return // Prevent disabling all options
        }
        _uiState.update { it.copy(useUppercase = newState) }
        generatePassword()
    }

    /**
     * Toggles lowercase characters.
     */
    fun toggleLowercase() {
        val newState = _uiState.value.useLowercase.not()
        if (!newState && !_uiState.value.useUppercase && !_uiState.value.useNumbers && !_uiState.value.useSymbols) {
            return // Prevent disabling all options
        }
        _uiState.update { it.copy(useLowercase = newState) }
        generatePassword()
    }

    /**
     * Toggles number characters.
     */
    fun toggleNumbers() {
        val newState = _uiState.value.useNumbers.not()
        if (!newState && !_uiState.value.useUppercase && !_uiState.value.useLowercase && !_uiState.value.useSymbols) {
            return // Prevent disabling all options
        }
        _uiState.update { it.copy(useNumbers = newState) }
        generatePassword()
    }

    /**
     * Toggles symbol characters.
     */
    fun toggleSymbols() {
        val newState = _uiState.value.useSymbols.not()
        if (!newState && !_uiState.value.useUppercase && !_uiState.value.useLowercase && !_uiState.value.useNumbers) {
            return // Prevent disabling all options
        }
        _uiState.update { it.copy(useSymbols = newState) }
        generatePassword()
    }

    /**
     * Marks password as copied (shows feedback for 2 seconds).
     */
    fun markPasswordAsCopied() {
        _uiState.update { it.copy(copiedPassword = true) }
    }

    /**
     * Clears the copied feedback.
     */
    fun clearCopiedFeedback() {
        _uiState.update { it.copy(copiedPassword = false) }
    }
}
