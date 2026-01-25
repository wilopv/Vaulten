package com.wilove.vaulten.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wilove.vaulten.domain.model.AlertSeverity
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.model.DashboardData
import com.wilove.vaulten.domain.model.SecurityAlert
import com.wilove.vaulten.ui.theme.VaultenTheme

/**
 * Dashboard screen composable.
 * Displays recent credentials, security alerts, and quick actions.
 *
 * @param uiState Current UI state
 * @param onCredentialClick Callback when a credential is clicked
 * @param onAddCredentialClick Callback when add credential button is clicked
 * @param onRefresh Callback when refresh is requested
 * @param modifier Optional modifier
 */
@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onCredentialClick: (String) -> Unit,
    onAddCredentialClick: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Vault Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            uiState.isLoading -> {
                // Loading state
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Loading dashboard...")
                }
            }

            uiState.errorMessage != null -> {
                // Error state
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRefresh) {
                        Text("Retry")
                    }
                }
            }

            uiState.dashboardData != null -> {
                // Success state
                DashboardContent(
                    dashboardData = uiState.dashboardData,
                    onCredentialClick = onCredentialClick,
                    onAddCredentialClick = onAddCredentialClick
                )
            }
        }
    }
}

/**
 * Dashboard content when data is loaded successfully.
 */
@Composable
private fun DashboardContent(
    dashboardData: DashboardData,
    onCredentialClick: (String) -> Unit,
    onAddCredentialClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Stats
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Total Credentials",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${dashboardData.totalCredentials}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Security Alerts
        if (dashboardData.securityAlerts.isNotEmpty()) {
            item {
                Text(
                    text = "Security Alerts",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(dashboardData.securityAlerts) { alert ->
                SecurityAlertCard(alert = alert)
            }
        }

        // Recent Credentials
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Accesses",
                    style = MaterialTheme.typography.titleLarge
                )
                Button(onClick = onAddCredentialClick) {
                    Text("Add")
                }
            }
        }

        if (dashboardData.recentCredentials.isEmpty()) {
            item {
                Text(
                    text = "No credentials yet. Add your first one!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(dashboardData.recentCredentials) { credential ->
                CredentialCard(
                    credential = credential,
                    onClick = { onCredentialClick(credential.id) }
                )
            }
        }
    }
}

/**
 * Card displaying a security alert.
 */
@Composable
private fun SecurityAlertCard(alert: SecurityAlert) {
    val containerColor = when (alert.severity) {
        AlertSeverity.CRITICAL -> MaterialTheme.colorScheme.errorContainer
        AlertSeverity.WARNING -> MaterialTheme.colorScheme.tertiaryContainer
        AlertSeverity.INFO -> MaterialTheme.colorScheme.secondaryContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = alert.severity.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = alert.message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Card displaying a credential summary.
 */
@Composable
private fun CredentialCard(
    credential: Credential,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = credential.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = credential.username,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (credential.url != null) {
                Text(
                    text = credential.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    VaultenTheme {
        DashboardScreen(
            uiState = DashboardUiState(
                isLoading = false,
                dashboardData = DashboardData(
                    recentCredentials = listOf(
                        Credential(
                            id = "1",
                            name = "Gmail",
                            username = "user@gmail.com",
                            password = "***",
                            url = "https://gmail.com"
                        )
                    ),
                    securityAlerts = listOf(
                        SecurityAlert(
                            id = "1",
                            message = "2 passwords are weak",
                            severity = AlertSeverity.WARNING
                        )
                    ),
                    totalCredentials = 5
                )
            ),
            onCredentialClick = {},
            onAddCredentialClick = {},
            onRefresh = {}
        )
    }
}
