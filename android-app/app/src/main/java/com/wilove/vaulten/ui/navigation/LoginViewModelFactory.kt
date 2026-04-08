package com.wilove.vaulten.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wilove.vaulten.data.local.TokenManager
import com.wilove.vaulten.domain.repository.AuthRepository
import com.wilove.vaulten.ui.login.LoginViewModel

class LoginViewModelFactory(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authRepository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
