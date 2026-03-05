package com.wilove.vaulten.domain.usecase

import com.wilove.vaulten.domain.model.DashboardData
import com.wilove.vaulten.domain.repository.VaultRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Use case for retrieving dashboard data as a reactive stream.
 */
class GetDashboardDataUseCase(
    private val repository: VaultRepository
) {
    /**
     * Executes the use case to retrieve dashboard data.
     */
    operator fun invoke(): Flow<DashboardData> {
        return combine(
            repository.getRecentCredentials(limit = 5),
            repository.getAllCredentials()
        ) { recent, all ->
            DashboardData(
                recentCredentials = recent,
                securityAlerts = emptyList(), // Alerts not yet implemented in repository
                totalCredentials = all.size
            )
        }
    }

    /**
     * Triggers a remote sync.
     */
    suspend fun sync() {
        repository.sync()
    }
}
