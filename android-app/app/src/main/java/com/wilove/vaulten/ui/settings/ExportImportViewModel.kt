package com.wilove.vaulten.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wilove.vaulten.domain.usecase.ExportVaultUseCase
import com.wilove.vaulten.domain.usecase.ImportVaultUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExportImportUiState(
    val isLoading: Boolean = false,
    // Non-null triggers the CSV CreateDocument launcher in the screen
    val exportPendingCsvContent: String? = null,
    // Non-null triggers the JSON CreateDocument launcher in the screen
    val exportPendingJsonContent: String? = null,
    val showEncryptionPasswordDialog: Boolean = false,
    val showDecryptionPasswordDialog: Boolean = false,
    // Raw file content waiting for a decryption password
    val pendingImportContent: String? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class ExportImportViewModel(
    private val exportVaultUseCase: ExportVaultUseCase,
    private val importVaultUseCase: ImportVaultUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportImportUiState())
    val uiState: StateFlow<ExportImportUiState> = _uiState.asStateFlow()

    // ── Export ───────────────────────────────────────────────────────────────

    fun exportAsCsv() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val csv = exportVaultUseCase.toCsv()
                _uiState.update { it.copy(isLoading = false, exportPendingCsvContent = csv) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al exportar: ${e.message}") }
            }
        }
    }

    fun requestEncryptedExport() {
        _uiState.update { it.copy(showEncryptionPasswordDialog = true) }
    }

    fun exportAsEncryptedJson(password: String) {
        _uiState.update { it.copy(showEncryptionPasswordDialog = false) }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val jsonContent = exportVaultUseCase.toEncryptedJson(password)
                _uiState.update { it.copy(isLoading = false, exportPendingJsonContent = jsonContent) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cifrar: ${e.message}") }
            }
        }
    }

    fun onCsvExportHandled() {
        _uiState.update { it.copy(exportPendingCsvContent = null) }
    }

    fun onJsonExportHandled() {
        _uiState.update { it.copy(exportPendingJsonContent = null) }
    }

    fun dismissEncryptionDialog() {
        _uiState.update { it.copy(showEncryptionPasswordDialog = false) }
    }

    // ── Import ───────────────────────────────────────────────────────────────

    /**
     * Called by the screen after reading the file content from the SAF URI.
     * Detects CSV vs encrypted JSON by content prefix.
     */
    fun onFileSelected(content: String) {
        val isCsv = !content.trimStart().startsWith("{")
        if (isCsv) {
            importCsv(content)
        } else {
            _uiState.update { it.copy(pendingImportContent = content, showDecryptionPasswordDialog = true) }
        }
    }

    private fun importCsv(content: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = importVaultUseCase.fromCsv(content)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = buildResultMessage(result.imported, result.skipped)
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al importar: ${e.message}") }
            }
        }
    }

    fun importEncryptedJson(password: String) {
        val content = _uiState.value.pendingImportContent ?: return
        _uiState.update { it.copy(showDecryptionPasswordDialog = false, pendingImportContent = null) }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = importVaultUseCase.fromEncryptedJson(content, password)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = buildResultMessage(result.imported, result.skipped)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Contraseña incorrecta o archivo inválido")
                }
            }
        }
    }

    fun dismissDecryptionDialog() {
        _uiState.update { it.copy(showDecryptionPasswordDialog = false, pendingImportContent = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }

    private fun buildResultMessage(imported: Int, skipped: Int): String {
        val importedText = if (imported == 1) "1 credencial importada" else "$imported credenciales importadas"
        return if (skipped == 0) importedText
        else "$importedText ($skipped duplicadas omitidas)"
    }
}
