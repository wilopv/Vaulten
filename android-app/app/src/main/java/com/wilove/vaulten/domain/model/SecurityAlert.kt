package com.wilove.vaulten.domain.model

/**
 * Domain model representing a security alert or notification.
 *
 * @property id Unique identifier for this alert
 * @property message Alert message to display to the user
 * @property severity Severity level (INFO, WARNING, CRITICAL)
 * @property timestamp When this alert was created
 */
data class SecurityAlert(
    val id: String,
    val message: String,
    val severity: AlertSeverity = AlertSeverity.INFO,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Severity levels for security alerts.
 */
enum class AlertSeverity {
    INFO,
    WARNING,
    CRITICAL
}
