package com.wilove.vaulten.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wilove.vaulten.domain.usecase.GetDashboardDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Dashboard.
 * Observes reactive dashboard data and handles background synchronization.
 */
class DashboardViewModel(
    private val getDashboardDataUseCase: GetDashboardDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeDashboardData()
        refreshDashboard()
    }

    /**
     * Observes the reactive flow of dashboard data from local persistence.
     */
    private fun observeDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getDashboardDataUseCase()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { data ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            dashboardData = data,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    /**
     * Triggers a remote synchronization in the background.
     */
    fun refreshDashboard() {
        viewModelScope.launch {
            try {
                getDashboardDataUseCase.sync()
            } catch (e: Exception) {
                // Background sync failure doesn't necessarily block UI since we have local data
                // But we could report it if it's a persistent issue.
            }
        }
    }
}
