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
import com.wilove.vaulten.domain.usecase.GetAllCredentialsUseCase
import com.wilove.vaulten.domain.usecase.GetCredentialByIdUseCase
import com.wilove.vaulten.domain.usecase.GetDashboardDataUseCase
import com.wilove.vaulten.ui.credentials.CredentialDetailScreen
import com.wilove.vaulten.ui.credentials.CredentialDetailViewModel
import com.wilove.vaulten.ui.credentials.CredentialsListScreen
import com.wilove.vaulten.ui.credentials.CredentialsListViewModel
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
    val getAllCredentialsUseCase = GetAllCredentialsUseCase(repository)
    val getCredentialByIdUseCase = GetCredentialByIdUseCase(repository)

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
                onViewAllClick = {
                    navController.navigate(VaultenDestinations.CREDENTIALS_LIST)
                },
                onRefresh = viewModel::refresh
            )
        }

        // Credentials List Screen
        composable(VaultenDestinations.CREDENTIALS_LIST) {
            val viewModel: CredentialsListViewModel = viewModel(
                factory = CredentialsListViewModelFactory(getAllCredentialsUseCase)
            )
            val uiState by viewModel.uiState.collectAsState()

            CredentialsListScreen(
                uiState = uiState,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onCredentialClick = { credentialId ->
                    navController.navigate(VaultenDestinations.credentialDetail(credentialId))
                },
                onAddCredentialClick = {
                    navController.navigate(VaultenDestinations.ADD_CREDENTIAL)
                },
                onRefresh = viewModel::refresh
            )
        }

        // Credential Detail Screen
        composable(VaultenDestinations.CREDENTIAL_DETAIL) { backStackEntry ->
            val credentialId = backStackEntry.arguments?.getString("credentialId") ?: return@composable
            val viewModel: CredentialDetailViewModel = viewModel(
                factory = CredentialDetailViewModelFactory(getCredentialByIdUseCase)
            )
            val uiState by viewModel.uiState.collectAsState()

            // Load credential when screen is first shown
            androidx.compose.runtime.LaunchedEffect(credentialId) {
                viewModel.loadCredential(credentialId)
            }

            CredentialDetailScreen(
                uiState = uiState,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = {
                    navController.navigate(VaultenDestinations.editCredential(credentialId))
                },
                onDeleteClick = {
                    // TODO: Implement delete credential
                    navController.popBackStack()
                },
                onCopyField = { fieldName, fieldValue ->
                    // TODO: Implement copy to clipboard
                    viewModel.markFieldAsCopied(fieldName)
                },
                onTogglePasswordVisibility = viewModel::togglePasswordVisibility
            )
        }

        // TODO: Add other screen destinations as they are implementedeen destinations as they are implemented
        // - Credential Detail
        // - Add/Edit Credential
        // - Password Generator
        // - Security Settings
    }
}
