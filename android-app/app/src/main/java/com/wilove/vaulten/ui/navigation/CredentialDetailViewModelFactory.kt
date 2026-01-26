package com.wilove.vaulten.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wilove.vaulten.domain.usecase.GetCredentialByIdUseCase
import com.wilove.vaulten.ui.credentials.CredentialDetailViewModel

class CredentialDetailViewModelFactory(
    private val getCredentialByIdUseCase: GetCredentialByIdUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CredentialDetailViewModel::class.java)) {
            return CredentialDetailViewModel(getCredentialByIdUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
