package com.wilove.vaulten.domain.usecase

import com.wilove.vaulten.domain.model.DashboardData
import com.wilove.vaulten.domain.repository.VaultRepository

/**
 * Use case for retrieving dashboard data.
 * Encapsulates the business logic for fetching and aggregating
 * recent credentials and security alerts.
 *
 * @property repository The vault repository for data access
 */
class GetDashboardDataUseCase(
    private val repository: VaultRepository
) {
    /**
     * Executes the use case to retrieve dashboard data.
     *
     * @return DashboardData containing recent credentials and alerts
     */
    suspend operator fun invoke(): DashboardData {
        val recentCredentials = repository.getRecentCredentials(limit = 5)
        val securityAlerts = repository.getSecurityAlerts()
        val allCredentials = repository.getAllCredentials()

        return DashboardData(
            recentCredentials = recentCredentials,
            securityAlerts = securityAlerts,
            totalCredentials = allCredentials.size
        )
    }
}
