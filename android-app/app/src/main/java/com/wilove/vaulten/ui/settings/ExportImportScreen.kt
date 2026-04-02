package com.wilove.vaulten.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportImportScreen(
    uiState: ExportImportUiState,
    onExportCsvClick: () -> Unit,
    onRequestEncryptedExportClick: () -> Unit,
    onExportEncryptedConfirm: (String) -> Unit,
    onExportEncryptedDismiss: () -> Unit,
    onImportEncryptedConfirm: (String) -> Unit,
    onImportEncryptedDismiss: () -> Unit,
    onFileSelected: (content: String) -> Unit,
    onCsvExportHandled: () -> Unit,
    onJsonExportHandled: () -> Unit,
    onBackClick: () -> Unit,
    onClearMessages: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // ── SAF: CSV export ──────────────────────────────────────────────────────
    val csvExportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val content = uiState.exportPendingCsvContent ?: return@rememberLauncherForActivityResult
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.write(content.toByteArray(Charsets.UTF_8))
        }
        onCsvExportHandled()
    }

    LaunchedEffect(uiState.exportPendingCsvContent) {
        if (uiState.exportPendingCsvContent != null) {
            csvExportLauncher.launch("vaulten_export.csv")
        }
    }

    // ── SAF: encrypted JSON export ───────────────────────────────────────────
    val jsonExportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val content = uiState.exportPendingJsonContent ?: return@rememberLauncherForActivityResult
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.write(content.toByteArray(Charsets.UTF_8))
        }
        onJsonExportHandled()
    }

    LaunchedEffect(uiState.exportPendingJsonContent) {
        if (uiState.exportPendingJsonContent != null) {
            jsonExportLauncher.launch("vaulten_export.json")
        }
    }

    // ── SAF: import ──────────────────────────────────────────────────────────
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val content = context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.readBytes().toString(Charsets.UTF_8)
        } ?: return@rememberLauncherForActivityResult
        onFileSelected(content)
    }

    // ── Password dialog: encryption ──────────────────────────────────────────
    if (uiState.showEncryptionPasswordDialog) {
        PasswordInputDialog(
            title = "Cifrar exportación",
            confirmLabel = "Exportar",
            hint = "Contraseña de cifrado",
            onConfirm = onExportEncryptedConfirm,
            onDismiss = onExportEncryptedDismiss
        )
    }

    // ── Password dialog: decryption ──────────────────────────────────────────
    if (uiState.showDecryptionPasswordDialog) {
        PasswordInputDialog(
            title = "Descifrar archivo",
            confirmLabel = "Importar",
            hint = "Contraseña usada al exportar",
            onConfirm = onImportEncryptedConfirm,
            onDismiss = onImportEncryptedDismiss
        )
    }

    // ── Result dialogs ───────────────────────────────────────────────────────
    if (uiState.successMessage != null) {
        AlertDialog(
            onDismissRequest = onClearMessages,
            title = { Text("Importación completada") },
            text = { Text(uiState.successMessage) },
            confirmButton = {
                TextButton(onClick = onClearMessages) { Text("Aceptar") }
            }
        )
    }

    if (uiState.errorMessage != null) {
        AlertDialog(
            onDismissRequest = onClearMessages,
            title = { Text("Error") },
            text = { Text(uiState.errorMessage) },
            confirmButton = {
                TextButton(onClick = onClearMessages) { Text("Aceptar") }
            }
        )
    }

    // ── Screen ───────────────────────────────────────────────────────────────
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exportar / Importar") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Procesando…", style = MaterialTheme.typography.bodyMedium)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ── Export section ────────────────────────────────────────────────
            Text(
                text = "Exportar bóveda",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CSV (sin cifrado)",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Exporta todas las credenciales en texto plano. Guárdalo en un lugar seguro.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onExportCsvClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Exportar CSV")
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "JSON cifrado (AES-256)",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Exporta la bóveda cifrada con una contraseña. Necesitarás esa contraseña para importarla.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRequestEncryptedExportClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Exportar JSON cifrado")
                    }
                }
            }

            // ── Import section ────────────────────────────────────────────────
            Text(
                text = "Importar bóveda",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Desde CSV o JSON cifrado",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Selecciona un archivo exportado anteriormente. Las credenciales duplicadas (misma URL + usuario) se omitirán.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { importLauncher.launch(arrayOf("*/*")) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Seleccionar archivo")
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordInputDialog(
    title: String,
    confirmLabel: String,
    hint: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(hint) },
                singleLine = true,
                visualTransformation = if (visible) VisualTransformation.None
                                       else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { visible = !visible }) {
                        Icon(
                            imageVector = if (visible) Icons.Default.VisibilityOff
                                          else Icons.Default.Visibility,
                            contentDescription = if (visible) "Ocultar" else "Mostrar"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (password.isNotEmpty()) onConfirm(password) },
                enabled = password.isNotEmpty()
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
