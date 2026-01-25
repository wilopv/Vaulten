package com.wilove.vaulten.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wilove.vaulten.domain.usecase.GetDashboardDataUseCase
import com.wilove.vaulten.ui.dashboard.DashboardViewModel

/**
 * Factory for creating DashboardViewModel with dependencies.
 * Required because DashboardViewModel has constructor parameters.
 *
 * @property getDashboardDataUseCase The use case dependency
 */
class DashboardViewModelFactory(
    private val getDashboardDataUseCase: GetDashboardDataUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(getDashboardDataUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
