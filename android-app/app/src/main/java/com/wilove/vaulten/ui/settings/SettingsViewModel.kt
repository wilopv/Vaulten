package com.wilove.vaulten.ui.settings

import android.content.Context
import android.util.Log
import android.view.autofill.AutofillManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(
    val isAutofillEnabled: Boolean = false
)

class SettingsViewModel(private val context: Context) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val autofillManager = context.getSystemService(AutofillManager::class.java)

    fun checkAutofillStatus() {
        // hasEnabledAutofillServices() returns true if ANY service is enabled, not specifically Vaulten.
        // Check the component name to verify Vaulten is the active autofill provider.
        val activeComponent = autofillManager?.autofillServiceComponentName
        val enabled = activeComponent?.packageName == context.packageName
        _uiState.value = _uiState.value.copy(isAutofillEnabled = enabled)
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
