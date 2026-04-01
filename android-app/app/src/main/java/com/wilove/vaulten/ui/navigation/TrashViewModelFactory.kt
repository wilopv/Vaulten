package com.wilove.vaulten.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wilove.vaulten.domain.usecase.GetDeletedCredentialsUseCase
import com.wilove.vaulten.domain.usecase.PermanentlyDeleteCredentialUseCase
import com.wilove.vaulten.domain.usecase.RestoreCredentialUseCase
import com.wilove.vaulten.ui.trash.TrashViewModel

class TrashViewModelFactory(
    private val getDeletedCredentialsUseCase: GetDeletedCredentialsUseCase,
    private val restoreCredentialUseCase: RestoreCredentialUseCase,
    private val permanentlyDeleteCredentialUseCase: PermanentlyDeleteCredentialUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrashViewModel(
                getDeletedCredentialsUseCase,
                restoreCredentialUseCase,
                permanentlyDeleteCredentialUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
