package com.wilove.vaulten.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wilove.vaulten.domain.usecase.ExportVaultUseCase
import com.wilove.vaulten.domain.usecase.ImportVaultUseCase
import com.wilove.vaulten.ui.settings.ExportImportViewModel

class ExportImportViewModelFactory(
    private val exportVaultUseCase: ExportVaultUseCase,
    private val importVaultUseCase: ImportVaultUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExportImportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExportImportViewModel(exportVaultUseCase, importVaultUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
