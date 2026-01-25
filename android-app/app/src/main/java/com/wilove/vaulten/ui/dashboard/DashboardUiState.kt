package com.wilove.vaulten.ui.dashboard

import com.wilove.vaulten.domain.model.DashboardData

/**
 * UI state for the Dashboard screen.
 * Represents all possible states the dashboard can be in.
 *
 * @property isLoading Whether data is currently being loaded
 * @property dashboardData The loaded dashboard data, null if not yet loaded
 * @property errorMessage Error message if loading failed, null otherwise
 */
data class DashboardUiState(
    val isLoading: Boolean = false,
    val dashboardData: DashboardData? = null,
    val errorMessage: String? = null
)
