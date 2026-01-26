package com.wilove.vaulten.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wilove.vaulten.domain.usecase.GeneratePasswordUseCase
import com.wilove.vaulten.ui.passwordgenerator.PasswordGeneratorViewModel

class PasswordGeneratorViewModelFactory(
    private val generatePasswordUseCase: GeneratePasswordUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordGeneratorViewModel::class.java)) {
            return PasswordGeneratorViewModel(generatePasswordUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
