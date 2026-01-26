package com.wilove.vaulten.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wilove.vaulten.domain.usecase.CreateCredentialUseCase
import com.wilove.vaulten.domain.usecase.GetCredentialByIdUseCase
import com.wilove.vaulten.domain.usecase.UpdateCredentialUseCase
import com.wilove.vaulten.ui.credentials.CreateEditCredentialViewModel

class CreateEditCredentialViewModelFactory(
    private val createCredentialUseCase: CreateCredentialUseCase,
    private val updateCredentialUseCase: UpdateCredentialUseCase,
    private val getCredentialByIdUseCase: GetCredentialByIdUseCase? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateEditCredentialViewModel::class.java)) {
            return CreateEditCredentialViewModel(
                createCredentialUseCase,
                updateCredentialUseCase,
                getCredentialByIdUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
