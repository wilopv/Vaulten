package com.wilove.vaulten.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wilove.vaulten.data.repository.FakeVaultRepository
import com.wilove.vaulten.domain.usecase.GetDashboardDataUseCase
import com.wilove.vaulten.ui.dashboard.DashboardScreen
import com.wilove.vaulten.ui.dashboard.DashboardViewModel
import com.wilove.vaulten.ui.login.LoginScreen
import com.wilove.vaulten.ui.login.LoginViewModel
import com.wilove.vaulten.ui.signup.SignupScreen
import com.wilove.vaulten.ui.signup.SignupViewModel


/**
 * Main navigation graph for the Vaulten app.
 * Defines all navigation routes and their corresponding screens.
 *
 * @param navController The navigation controller for managing navigation
 * @param modifier Optional modifier for the NavHost
 * @param startDestination The initial destination when the app starts
 */
@Composable
fun VaultenNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = VaultenDestinations.LOGIN
) {
    // Create repository and use case instances
    // In a real app, these would be injected via Hilt or similar
    val repository = FakeVaultRepository()
    val getDashboardDataUseCase = GetDashboardDataUseCase(repository)

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Login Screen
        composable(VaultenDestinations.LOGIN) {
            val viewModel: LoginViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            LoginScreen(
                uiState = uiState,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onUnlockClick = {
                    if (viewModel.onUnlockClick()) {
                        // Navigate to dashboard on successful unlock
                        // In real app, this would wait for actual authentication
                        navController.navigate(VaultenDestinations.DASHBOARD) {
                            popUpTo(VaultenDestinations.LOGIN) { inclusive = true }
                        }
                    }
                },
                onBiometricToggle = viewModel::onBiometricToggle,
                onSignupClick = {
                    navController.navigate(VaultenDestinations.SIGNUP)
                }
            )
        }

        // Signup Screen
        composable(VaultenDestinations.SIGNUP) {
            val viewModel: SignupViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            SignupScreen(
                uiState = uiState,
                onFullNameChange = viewModel::onFullNameChange,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                onSignupClick = {
                    if (viewModel.onSignupClick()) {
                        // Navigate to dashboard on successful signup
                        // In real app, this would wait for actual account creation
                        navController.navigate(VaultenDestinations.DASHBOARD) {
                            popUpTo(VaultenDestinations.LOGIN) { inclusive = true }
                        }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // Dashboard Screen
        composable(VaultenDestinations.DASHBOARD) {
            val viewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModelFactory(getDashboardDataUseCase)
            )
            val uiState by viewModel.uiState.collectAsState()

            DashboardScreen(
                uiState = uiState,
                onCredentialClick = { credentialId ->
                    navController.navigate(VaultenDestinations.credentialDetail(credentialId))
                },
                onAddCredentialClick = {
                    navController.navigate(VaultenDestinations.ADD_CREDENTIAL)
                },
                onRefresh = viewModel::refresh
            )
        }

        // TODO: Add other screen destinations as they are implemented
        // - Credentials List
        // - Credential Detail
        // - Add/Edit Credential
        // - Password Generator
        // - Security Settings
    }
}
