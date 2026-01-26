package com.wilove.vaulten.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wilove.vaulten.domain.usecase.GetAllCredentialsUseCase
import com.wilove.vaulten.ui.credentials.CredentialsListViewModel

class CredentialsListViewModelFactory(
    private val getAllCredentialsUseCase: GetAllCredentialsUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CredentialsListViewModel::class.java)) {
            return CredentialsListViewModel(getAllCredentialsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
