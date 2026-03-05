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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onCredentialClick: (String) -> Unit,
    onAddCredentialClick: () -> Unit,
    onViewAllClick: () -> Unit,
    onRefresh: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vault Dashboard",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when {
                uiState.isLoading -> {
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
                    DashboardContent(
                        dashboardData = uiState.dashboardData,
                        onCredentialClick = onCredentialClick,
                        onAddCredentialClick = onAddCredentialClick,
                        onViewAllClick = onViewAllClick
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(
    dashboardData: DashboardData,
    onCredentialClick: (String) -> Unit,
    onAddCredentialClick: () -> Unit,
    onViewAllClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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

        item {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Accesses",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    androidx.compose.material3.TextButton(onClick = onViewAllClick) {
                        Text("View all")
                    }
                    Button(onClick = onAddCredentialClick) {
                        Text("Add")
                    }
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
            onViewAllClick = {},
            onRefresh = {},
            onLogoutClick = {}
        )
    }
}
