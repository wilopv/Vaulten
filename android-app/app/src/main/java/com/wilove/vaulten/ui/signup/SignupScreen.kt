package com.wilove.vaulten.ui.signup

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wilove.vaulten.ui.theme.VaultenTheme

/**
 * Stateless Signup UI. Renders purely from [SignupUiState] and forwards user events
 * via the provided callbacks.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    uiState: SignupUiState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSignupClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Create Vault",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.testTag(SignupTestTags.Title)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sign up to start protecting your credentials.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.fullName,
            onValueChange = onFullNameChange,
            label = { Text(text = "Full name") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(SignupTestTags.FullNameField)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text(text = "Email") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(SignupTestTags.EmailField)
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
                .testTag(SignupTestTags.PasswordField)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text(text = "Confirm password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(SignupTestTags.ConfirmPasswordField)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag(SignupTestTags.ErrorText)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        val signupEnabled = !uiState.isLoading

        Button(
            onClick = onSignupClick,
            enabled = signupEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(SignupTestTags.SignupButton)
        ) {
            Text(text = if (uiState.isLoading) "Creating..." else "Create account")
        }

        TextButton(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(SignupTestTags.LoginButton)
        ) {
            Text(text = "Already have an account? Sign in")
        }

        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(12.dp))
            CircularProgressIndicator(
                modifier = Modifier.testTag(SignupTestTags.Loading)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignupScreenPreview() {
    VaultenTheme {
        SignupScreen(
            uiState = SignupUiState(),
            onFullNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onSignupClick = {},
            onLoginClick = {}
        )
    }
}
