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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wilove.vaulten.ui.theme.VaultenTheme

/**
 * Stateless Create/Edit Credential UI. Renders purely from [CreateEditCredentialUiState].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditCredentialScreen(
    uiState: CreateEditCredentialUiState,
    onNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onUrlChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onGeneratePasswordClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (uiState.isEditMode) "Edit Credential" else "Add Credential",
                    modifier = Modifier.testTag(CreateEditCredentialTestTags.Title)
                )
            },
            navigationIcon = {
                IconButton(onClick = onCancelClick) {
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
                        modifier = Modifier.testTag(CreateEditCredentialTestTags.Loading)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Loading credential...")
                }
            }

            uiState.savedSuccessfully -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Credential saved successfully!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag(CreateEditCredentialTestTags.SuccessMessage)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onCancelClick) {
                        Text("Done")
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Error message
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.testTag(CreateEditCredentialTestTags.ErrorMessage)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Name field
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = onNameChange,
                        label = { Text("Name / Service") },
                        isError = uiState.nameError != null,
                        supportingText = if (uiState.nameError != null) {
                            { Text(uiState.nameError) }
                        } else null,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(CreateEditCredentialTestTags.NameField)
                    )

                    // Username field
                    OutlinedTextField(
                        value = uiState.username,
                        onValueChange = onUsernameChange,
                        label = { Text("Username / Email") },
                        isError = uiState.usernameError != null,
                        supportingText = if (uiState.usernameError != null) {
                            { Text(uiState.usernameError) }
                        } else null,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(CreateEditCredentialTestTags.UsernameField)
                    )

                    // Password field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = onPasswordChange,
                            label = { Text("Password") },
                            isError = uiState.passwordError != null,
                            supportingText = if (uiState.passwordError != null) {
                                { Text(uiState.passwordError) }
                            } else null,
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag(CreateEditCredentialTestTags.PasswordField)
                        )
                        OutlinedButton(
                            onClick = onGeneratePasswordClick,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .testTag(CreateEditCredentialTestTags.GeneratePasswordButton)
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Generate",
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text("Generate")
                        }
                    }

                    // URL field
                    OutlinedTextField(
                        value = uiState.url,
                        onValueChange = onUrlChange,
                        label = { Text("Website / App URL (optional)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(CreateEditCredentialTestTags.UrlField)
                    )
                }

                // Action buttons at the bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancelClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag(CreateEditCredentialTestTags.CancelButton)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = onSaveClick,
                        enabled = !uiState.isSaving,
                        modifier = Modifier
                            .weight(1f)
                            .testTag(CreateEditCredentialTestTags.SaveButton)
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .height(20.dp)
                                    .padding(end = 8.dp)
                            )
                        }
                        Text(if (uiState.isEditMode) "Update" else "Save")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateEditCredentialScreenPreview() {
    VaultenTheme {
        CreateEditCredentialScreen(
            uiState = CreateEditCredentialUiState(isEditMode = false),
            onNameChange = {},
            onUsernameChange = {},
            onPasswordChange = {},
            onUrlChange = {},
            onSaveClick = {},
            onCancelClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateEditCredentialScreenEditPreview() {
    VaultenTheme {
        CreateEditCredentialScreen(
            uiState = CreateEditCredentialUiState(
                isEditMode = true,
                name = "Gmail",
                username = "user@gmail.com",
                password = "password123",
                url = "https://gmail.com"
            ),
            onNameChange = {},
            onUsernameChange = {},
            onPasswordChange = {},
            onUrlChange = {},
            onSaveClick = {},
            onCancelClick = {}
        )
    }
}
