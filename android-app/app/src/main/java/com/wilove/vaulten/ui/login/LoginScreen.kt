package com.wilove.vaulten.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wilove.vaulten.ui.theme.VaultenTheme
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree

/**
 * Stateless Login UI. Renders purely from [LoginUiState] and forwards user events
 * via the provided callbacks.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onUnlockClick: () -> Unit,
    onBiometricToggle: (Boolean) -> Unit,
    onSignupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
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

        val autofill = LocalAutofill.current
        val autofillTree = LocalAutofillTree.current

        // Email autofill wiring
        val emailAutofillNode = AutofillNode(
            autofillTypes = listOf(AutofillType.EmailAddress, AutofillType.Username),
            onFill = onEmailChange
        )
        autofillTree += emailAutofillNode

        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text(text = "Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(LoginTestTags.EmailField)
                .onGloballyPositioned {
                    emailAutofillNode.boundingBox = it.boundsInWindow()
                }
                .onFocusChanged {
                    autofill?.run {
                        if (it.isFocused) requestAutofillForNode(emailAutofillNode)
                        else cancelAutofillForNode(emailAutofillNode)
                    }
                }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password autofill wiring
        val passwordAutofillNode = AutofillNode(
            autofillTypes = listOf(AutofillType.Password),
            onFill = onPasswordChange
        )
        autofillTree += passwordAutofillNode

        OutlinedTextField(
            value = uiState.masterPassword,
            onValueChange = onPasswordChange,
            label = { Text(text = "Master password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(LoginTestTags.PasswordField)
                .onGloballyPositioned {
                    passwordAutofillNode.boundingBox = it.boundsInWindow()
                }
                .onFocusChanged {
                    autofill?.run {
                        if (it.isFocused) requestAutofillForNode(passwordAutofillNode)
                        else cancelAutofillForNode(passwordAutofillNode)
                    }
                }
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

        TextButton(
            onClick = onSignupClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(LoginTestTags.SignupButton)
        ) {
            Text(text = "Create an account")
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
            onBiometricToggle = {},
            onSignupClick = {}
        )
    }
}
