package com.wilove.vaulten.ui.credentials

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.ui.theme.VaultenTheme

/**
 * Stateless Credentials List UI. Renders purely from [CredentialsListUiState].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialsListScreen(
    uiState: CredentialsListUiState,
    onSearchQueryChange: (String) -> Unit,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Credentials",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.testTag(CredentialsListTestTags.Title)
            )
            Button(
                onClick = onAddCredentialClick,
                modifier = Modifier.testTag(CredentialsListTestTags.AddButton)
            ) {
                Text(text = "Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text(text = "Search") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(CredentialsListTestTags.SearchField)
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.testTag(CredentialsListTestTags.Loading)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Loading credentials...")
                }
            }

            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag(CredentialsListTestTags.ErrorText)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRefresh,
                        modifier = Modifier.testTag(CredentialsListTestTags.RetryButton)
                    ) {
                        Text(text = "Retry")
                    }
                }
            }

            uiState.credentials.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No credentials found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag(CredentialsListTestTags.EmptyText)
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(CredentialsListTestTags.List),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.credentials) { credential ->
                        CredentialRow(
                            credential = credential,
                            onClick = { onCredentialClick(credential.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CredentialRow(
    credential: Credential,
    onClick: () -> Unit
) {
    androidx.compose.material3.Card(
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
private fun CredentialsListScreenPreview() {
    VaultenTheme {
        CredentialsListScreen(
            uiState = CredentialsListUiState(
                credentials = listOf(
                    Credential(
                        id = "1",
                        name = "Gmail",
                        username = "user@gmail.com",
                        password = "***",
                        url = "https://gmail.com"
                    )
                )
            ),
            onSearchQueryChange = {},
            onCredentialClick = {},
            onAddCredentialClick = {},
            onRefresh = {}
        )
    }
}
