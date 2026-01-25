package com.wilove.vaulten.domain.model

/**
 * Domain model representing dashboard data.
 * Aggregates recent credentials and security alerts for the home screen.
 *
 * @property recentCredentials List of recently accessed credentials
 * @property securityAlerts List of active security alerts
 * @property totalCredentials Total number of stored credentials
 */
data class DashboardData(
    val recentCredentials: List<Credential> = emptyList(),
    val securityAlerts: List<SecurityAlert> = emptyList(),
    val totalCredentials: Int = 0
)
