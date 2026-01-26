package com.wilove.vaulten.ui.credentials

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.ui.theme.VaultenTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Stateless Credential Detail UI. Renders purely from [CredentialDetailUiState].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialDetailScreen(
    uiState: CredentialDetailUiState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCopyField: (String, String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Credential Details",
                    modifier = Modifier.testTag(CredentialDetailTestTags.Title)
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.testTag(CredentialDetailTestTags.Loading)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Loading credential...")
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
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.testTag(CredentialDetailTestTags.ErrorMessage)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBackClick) {
                        Text("Go Back")
                    }
                }
            }

            uiState.credential != null -> {
                CredentialDetailContent(
                    credential = uiState.credential,
                    passwordVisible = uiState.passwordVisible,
                    copiedField = uiState.copiedField,
                    onCopyField = onCopyField,
                    onTogglePasswordVisibility = onTogglePasswordVisibility,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                )

                // Action buttons at the bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onEditClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag(CredentialDetailTestTags.EditButton)
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text("Edit")
                    }

                    OutlinedButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag(CredentialDetailTestTags.DeleteButton)
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text("Delete")
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No credential found")
                }
            }
        }
    }
}

@Composable
private fun CredentialDetailContent(
    credential: Credential,
    passwordVisible: Boolean,
    copiedField: String?,
    onCopyField: (String, String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Name
        CredentialDetailField(
            label = "Name",
            value = credential.name,
            onCopy = { onCopyField("name", credential.name) },
            isCopied = copiedField == "name",
            testTag = CredentialDetailTestTags.NameField
        )

        // URL
        if (credential.url != null) {
            CredentialDetailField(
                label = "Website / App",
                value = credential.url,
                onCopy = { onCopyField("url", credential.url) },
                isCopied = copiedField == "url",
                testTag = CredentialDetailTestTags.UrlField
            )
        }

        // Username
        CredentialDetailField(
            label = "Username / Email",
            value = credential.username,
            onCopy = { onCopyField("username", credential.username) },
            isCopied = copiedField == "username",
            testTag = CredentialDetailTestTags.UsernameField
        )

        // Password
        PasswordDetailField(
            password = credential.password,
            isVisible = passwordVisible,
            onToggleVisibility = onTogglePasswordVisibility,
            onCopy = { onCopyField("password", credential.password) },
            isCopied = copiedField == "password",
            testTag = CredentialDetailTestTags.PasswordField
        )

        // Last Modified
        val dateFormatter = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())
        val lastModifiedText = dateFormatter.format(Date(credential.lastModified))
        CredentialDetailField(
            label = "Last Modified",
            value = lastModifiedText,
            onCopy = null,
            isCopied = false,
            testTag = CredentialDetailTestTags.LastModifiedField,
            copyable = false
        )
    }
}

@Composable
private fun CredentialDetailField(
    label: String,
    value: String,
    onCopy: (() -> Unit)?,
    isCopied: Boolean,
    testTag: String,
    copyable: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SelectionContainer(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .testTag(testTag)
                        .padding(end = 8.dp)
                )
            }
            if (copyable && onCopy != null) {
                if (isCopied) {
                    Text(
                        text = "Copied!",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag(CredentialDetailTestTags.CopiedIndicator)
                    )
                } else {
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.testTag("${testTag}CopyButton")
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy $label"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordDetailField(
    password: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    onCopy: (() -> Unit)?,
    isCopied: Boolean,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Password",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SelectionContainer(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isVisible) password else "•".repeat(password.length),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .testTag(testTag)
                        .padding(end = 8.dp)
                )
            }
            Row {
                IconButton(
                    onClick = onToggleVisibility,
                    modifier = Modifier.testTag(CredentialDetailTestTags.PasswordToggle)
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = if (isVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (isVisible) "Hide password" else "Show password"
                    )
                }
                if (isCopied) {
                    Text(
                        text = "Copied!",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag(CredentialDetailTestTags.CopiedIndicator)
                    )
                } else {
                    IconButton(
                        onClick = { onCopy?.invoke() },
                        modifier = Modifier.testTag("${testTag}CopyButton")
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy password"
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CredentialDetailScreenPreview() {
    VaultenTheme {
        CredentialDetailScreen(
            uiState = CredentialDetailUiState(
                credential = Credential(
                    id = "1",
                    name = "Gmail",
                    username = "user@gmail.com",
                    password = "securePassword123!",
                    url = "https://gmail.com",
                    lastModified = System.currentTimeMillis()
                ),
                passwordVisible = false
            ),
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onCopyField = { _, _ -> },
            onTogglePasswordVisibility = {}
        )
    }
}
