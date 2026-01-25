package com.wilove.vaulten.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wilove.vaulten.ui.theme.VaultenTheme

/**
 * Stateless Login UI. Renders purely from [LoginUiState] and forwards user events
 * via the provided callbacks.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onUnlockClick: () -> Unit,
    onBiometricToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Unlock Vault",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.testTag(LoginTestTags.Title)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter your master password to continue.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text(text = "Email") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(LoginTestTags.EmailField)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.masterPassword,
            onValueChange = onPasswordChange,
            label = { Text(text = "Master password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(LoginTestTags.PasswordField)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Biometric unlock",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            Switch(
                checked = uiState.biometricEnabled,
                onCheckedChange = onBiometricToggle,
                modifier = Modifier.testTag(LoginTestTags.BiometricToggle)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLockedOut) {
            Text(
                text = "Too many attempts. Please wait before trying again.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag(LoginTestTags.LockoutText)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = "Attempts remaining: ${uiState.remainingAttempts}/${uiState.maxAttempts}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.testTag(LoginTestTags.AttemptsText)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag(LoginTestTags.ErrorText)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Disable the primary action when locked or loading to reflect security constraints.
        val unlockEnabled = !uiState.isLockedOut && !uiState.isLoading

        Button(
            onClick = onUnlockClick,
            enabled = unlockEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(LoginTestTags.UnlockButton)
        ) {
            Text(text = if (uiState.isLoading) "Unlocking..." else "Unlock")
        }

        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(12.dp))
            CircularProgressIndicator(
                modifier = Modifier.testTag(LoginTestTags.Loading)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    VaultenTheme {
        LoginScreen(
            uiState = LoginUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onUnlockClick = {},
            onBiometricToggle = {}
        )
    }
}
