package com.wilove.vaulten.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wilove.vaulten.domain.usecase.GetDashboardDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Dashboard screen.
 * Manages dashboard state and coordinates data loading through use cases.
 *
 * @property getDashboardDataUseCase Use case for fetching dashboard data
 */
class DashboardViewModel(
    private val getDashboardDataUseCase: GetDashboardDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    /**
     * Loads dashboard data from the repository via the use case.
     * Updates UI state with loading, success, or error states.
     */
    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val dashboardData = getDashboardDataUseCase()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        dashboardData = dashboardData,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load dashboard: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Refreshes the dashboard data.
     * Can be called by pull-to-refresh or manual refresh actions.
     */
    fun refresh() {
        loadDashboardData()
    }
}
