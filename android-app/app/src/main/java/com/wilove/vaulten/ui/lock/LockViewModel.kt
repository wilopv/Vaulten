package com.wilove.vaulten.ui.lock

import androidx.lifecycle.ViewModel
import com.wilove.vaulten.data.local.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LockUiState(
    val errorMessage: String? = null
)

class LockViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LockUiState())
    val uiState: StateFlow<LockUiState> = _uiState.asStateFlow()

    /** Called by the composable after a successful biometric / device-credential authentication. */
    fun onUnlockSuccess() {
        tokenManager.saveLastActiveTimestamp(System.currentTimeMillis())
        _uiState.update { it.copy(errorMessage = null) }
    }

    /** Called when the biometric prompt returns a non-cancellation error. */
    fun onUnlockError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }
}
