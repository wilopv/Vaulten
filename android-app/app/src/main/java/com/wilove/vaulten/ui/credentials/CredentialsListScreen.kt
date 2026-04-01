package com.wilove.vaulten.ui.credentials

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.model.CredentialFilter
import com.wilove.vaulten.domain.model.PasswordHealthStatus
import com.wilove.vaulten.ui.theme.VaultenTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

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
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onTrashClick: () -> Unit = {},
    onApplyFilter: (CredentialFilter) -> Unit = {},
    onClearFilter: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showFilterSheet by remember { mutableStateOf(false) }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState
        ) {
            FilterBottomSheetContent(
                currentFilter = uiState.activeFilter,
                onApply = { filter ->
                    onApplyFilter(filter)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showFilterSheet = false
                    }
                },
                onClear = {
                    onClearFilter()
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showFilterSheet = false
                    }
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Credentials",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag(CredentialsListTestTags.Title)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtros",
                            tint = if (uiState.isFilterActive)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onTrashClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Papelera"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCredentialClick,
                modifier = Modifier.testTag(CredentialsListTestTags.AddButton)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Credential")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
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
                                healthStatus = uiState.passwordHealth[credential.id],
                                onClick = { onCredentialClick(credential.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CredentialRow(
    credential: Credential,
    healthStatus: PasswordHealthStatus?,
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
            if (healthStatus?.hasIssues == true) {
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (healthStatus.isDuplicate) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Duplicada",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFF9800)
                            )
                        }
                    }
                    if (healthStatus.isWeak) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Débil",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterBottomSheetContent(
    currentFilter: CredentialFilter,
    onApply: (CredentialFilter) -> Unit,
    onClear: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    var domain by remember { mutableStateOf(currentFilter.domain ?: "") }
    var afterText by remember {
        mutableStateOf(currentFilter.modifiedAfter?.let { dateFormat.format(it) } ?: "")
    }
    var beforeText by remember {
        mutableStateOf(currentFilter.modifiedBefore?.let { dateFormat.format(it) } ?: "")
    }
    var afterError by remember { mutableStateOf(false) }
    var beforeError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 8.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Filtros",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = domain,
            onValueChange = { domain = it },
            label = { Text("Dominio (ej. github.com)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = afterText,
            onValueChange = {
                afterText = it
                afterError = false
            },
            label = { Text("Modificada después de (dd/MM/yyyy)") },
            singleLine = true,
            isError = afterError,
            supportingText = if (afterError) ({ Text("Formato inválido") }) else null,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = beforeText,
            onValueChange = {
                beforeText = it
                beforeError = false
            },
            label = { Text("Modificada antes de (dd/MM/yyyy)") },
            singleLine = true,
            isError = beforeError,
            supportingText = if (beforeError) ({ Text("Formato inválido") }) else null,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onClear,
                modifier = Modifier.weight(1f)
            ) {
                Text("Limpiar")
            }

            Button(
                onClick = {
                    val after = afterText.trim().let { text ->
                        if (text.isEmpty()) null
                        else try { dateFormat.parse(text)?.time }
                        catch (e: Exception) { afterError = true; return@Button }
                    }
                    val before = beforeText.trim().let { text ->
                        if (text.isEmpty()) null
                        else try { dateFormat.parse(text)?.time }
                        catch (e: Exception) { beforeError = true; return@Button }
                    }
                    onApply(
                        CredentialFilter(
                            domain = domain.trim().ifBlank { null },
                            modifiedAfter = after,
                            modifiedBefore = before
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Aplicar")
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
            onBackClick = {},
            onRefresh = {}
        )
    }
}
