package com.wilove.vaulten.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wilove.vaulten.domain.usecase.GetAllCredentialsUseCase
import com.wilove.vaulten.domain.usecase.PasswordHealthUseCase
import com.wilove.vaulten.ui.credentials.CredentialsListViewModel

class CredentialsListViewModelFactory(
    private val getAllCredentialsUseCase: GetAllCredentialsUseCase,
    private val passwordHealthUseCase: PasswordHealthUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CredentialsListViewModel::class.java)) {
            return CredentialsListViewModel(getAllCredentialsUseCase, passwordHealthUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
