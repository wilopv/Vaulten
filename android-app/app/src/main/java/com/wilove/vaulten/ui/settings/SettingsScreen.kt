package com.wilove.vaulten.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

private const val TAG = "SettingsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onBackClick: () -> Unit,
    onEnableAutofillClick: () -> Unit, // kept for API compatibility; intent is now handled internally
    onCheckStatus: () -> Unit
) {
    val ctx = LocalContext.current

    // Re-check autofill status whenever the user returns from another screen (e.g. Settings).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) onCheckStatus()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var showGuide by remember { mutableStateOf(false) }

    if (showGuide) {
        val isSamsung = Build.MANUFACTURER.equals("samsung", ignoreCase = true)

        AlertDialog(
            onDismissRequest = { showGuide = false },
            title = { Text("Habilitar autorrelleno de Vaulten") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (isSamsung) {
                        Text(
                            text = "Samsung (OneUI)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("Ajustes → Administración general → Contraseñas y autorrelleno → Servicio de autorrelleno → Vaulten")
                        HorizontalDivider()
                    }
                    Text(
                        text = "Android (estándar)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Ajustes → Sistema → Idioma e introducción de texto → Servicio de autorrelleno → Vaulten")
                    Text(
                        text = "Pulsa \"Abrir ajustes\" e identifica la opción en tu dispositivo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    showGuide = false
                    openAutofillSettings(ctx.packageName) { intent ->
                        try {
                            ctx.startActivity(intent)
                        } catch (e: Exception) {
                            Log.e(TAG, "startActivity failed for $intent", e)
                        }
                    }
                }) {
                    Text("Abrir ajustes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGuide = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Autofill",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (uiState.isAutofillEnabled) "✓ Vaulten Autofill activo"
                               else "Enable Vaulten Autofill",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Allow Vaulten to automatically fill your usernames and passwords in other apps.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showGuide = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Autofill Settings")
                    }
                }
            }
        }
    }
}

/**
 * Builds the ordered list of intents to try when opening autofill settings,
 * and calls [launch] with each until one succeeds.
 *
 * Priority:
 * 1. ACTION_REQUEST_SET_AUTOFILL_SERVICE with package URI  — standard, shows service picker
 * 2. ACTION_REQUEST_SET_AUTOFILL_SERVICE without URI       — broader fallback
 * 3. ACTION_SETTINGS                                       — always available
 */
private fun openAutofillSettings(packageName: String, launch: (Intent) -> Unit) {
    val candidates = listOf(
        Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE).apply {
            data = Uri.parse("package:$packageName")
        },
        Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE),
        Intent(Settings.ACTION_SETTINGS)
    )

    for (intent in candidates) {
        try {
            launch(intent)
            Log.d(TAG, "Launched: ${intent.action} data=${intent.data}")
            return
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "Not handled: ${intent.action}", e)
        }
    }
    Log.e(TAG, "No settings intent could be resolved")
}
