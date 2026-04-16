package com.wilove.vaulten.ui.login

import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.wilove.vaulten.ui.theme.VaultenTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onUnlockClick: () -> Unit,
    onBiometricLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    // Incrementing this triggers a new biometric prompt
    var biometricTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(biometricTrigger) {
        if (biometricTrigger == 0) return@LaunchedEffect
        activity ?: return@LaunchedEffect

        val executor = ContextCompat.getMainExecutor(activity)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onBiometricLoginSuccess()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                // Cancellation / negative button — user chose to type manually, do nothing
            }
        }
        val prompt = BiometricPrompt(activity, executor, callback)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Iniciar sesión en Vaulten")
            .setSubtitle("Usa tu huella o el bloqueo del dispositivo")
            .setAllowedAuthenticators(
                androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or
                androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()
        prompt.authenticate(promptInfo)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Bienvenido a Vaulten",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.testTag(LoginTestTags.Title)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Introduce tus credenciales para acceder.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        val autofill = LocalAutofill.current
        val autofillTree = LocalAutofillTree.current

        val emailAutofillNode = AutofillNode(
            autofillTypes = listOf(AutofillType.EmailAddress, AutofillType.Username),
            onFill = onEmailChange
        )
        autofillTree += emailAutofillNode

        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(LoginTestTags.EmailField)
                .onGloballyPositioned { emailAutofillNode.boundingBox = it.boundsInWindow() }
                .onFocusChanged {
                    autofill?.run {
                        if (it.isFocused) requestAutofillForNode(emailAutofillNode)
                        else cancelAutofillForNode(emailAutofillNode)
                    }
                }
        )

        Spacer(modifier = Modifier.height(12.dp))

        val passwordAutofillNode = AutofillNode(
            autofillTypes = listOf(AutofillType.Password),
            onFill = onPasswordChange
        )
        autofillTree += passwordAutofillNode

        OutlinedTextField(
            value = uiState.masterPassword,
            onValueChange = onPasswordChange,
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(LoginTestTags.PasswordField)
                .onGloballyPositioned { passwordAutofillNode.boundingBox = it.boundsInWindow() }
                .onFocusChanged {
                    autofill?.run {
                        if (it.isFocused) requestAutofillForNode(passwordAutofillNode)
                        else cancelAutofillForNode(passwordAutofillNode)
                    }
                }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag(LoginTestTags.ErrorText)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = onUnlockClick,
            enabled = !uiState.isLockedOut && !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(LoginTestTags.UnlockButton)
        ) {
            Text(if (uiState.isLoading) "Iniciando sesión…" else "Iniciar sesión")
        }

        if (uiState.hasBiometricCredentials) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { biometricTrigger++ },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(LoginTestTags.BiometricButton)
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar sesión con huella dactilar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onSignupClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(LoginTestTags.SignupButton)
        ) {
            Text("Crear una cuenta")
        }

        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(12.dp))
            CircularProgressIndicator(modifier = Modifier.testTag(LoginTestTags.Loading))
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
            onBiometricLoginSuccess = {},
            onSignupClick = {}
        )
    }
}
