package com.wilove.vaulten.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wilove.vaulten.data.repository.AuthRepositoryImpl
import com.wilove.vaulten.data.repository.VaultRepositoryImpl
import com.wilove.vaulten.di.NetworkModule
import com.wilove.vaulten.domain.usecase.CreateCredentialUseCase
import com.wilove.vaulten.domain.usecase.GeneratePasswordUseCase
import com.wilove.vaulten.domain.usecase.GetAllCredentialsUseCase
import com.wilove.vaulten.domain.usecase.GetCredentialByIdUseCase
import com.wilove.vaulten.domain.usecase.GetDashboardDataUseCase
import com.wilove.vaulten.domain.usecase.UpdateCredentialUseCase
import com.wilove.vaulten.ui.credentials.CreateEditCredentialScreen
import com.wilove.vaulten.ui.credentials.CreateEditCredentialViewModel
import com.wilove.vaulten.ui.credentials.CredentialDetailScreen
import com.wilove.vaulten.ui.credentials.CredentialDetailViewModel
import com.wilove.vaulten.ui.credentials.CredentialsListScreen
import com.wilove.vaulten.ui.credentials.CredentialsListViewModel
import com.wilove.vaulten.ui.dashboard.DashboardScreen
import com.wilove.vaulten.ui.dashboard.DashboardViewModel
import com.wilove.vaulten.ui.login.LoginScreen
import com.wilove.vaulten.ui.login.LoginViewModel
import com.wilove.vaulten.ui.passwordgenerator.PasswordGeneratorScreen
import com.wilove.vaulten.ui.passwordgenerator.PasswordGeneratorViewModel
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
    // Get context for TokenManager
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Create real repository and use case instances via manual DI
    val tokenManager = NetworkModule.provideTokenManager(context)
    val okHttpClient = NetworkModule.provideOkHttpClient(tokenManager)
    
    val authRepository = com.wilove.vaulten.data.repository.AuthRepositoryImpl(
        NetworkModule.provideAuthApiService(okHttpClient),
        tokenManager
    )
    
    val vaultRepository = com.wilove.vaulten.data.repository.VaultRepositoryImpl(
        NetworkModule.provideVaultApiService(okHttpClient)
    )
    
    val getDashboardDataUseCase = GetDashboardDataUseCase(vaultRepository)
    val getAllCredentialsUseCase = GetAllCredentialsUseCase(vaultRepository)
    val getCredentialByIdUseCase = GetCredentialByIdUseCase(vaultRepository)
    val createCredentialUseCase = CreateCredentialUseCase(vaultRepository)
    val updateCredentialUseCase = UpdateCredentialUseCase(vaultRepository)
    val generatePasswordUseCase = GeneratePasswordUseCase()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Login Screen
        composable(VaultenDestinations.LOGIN) {
            val viewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(authRepository)
            )
            val uiState by viewModel.uiState.collectAsState()

            LoginScreen(
                uiState = uiState,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onUnlockClick = {
                    viewModel.onUnlockClick {
                        // Navigate to dashboard on successful unlock
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
            val viewModel: SignupViewModel = viewModel(
                factory = SignupViewModelFactory(authRepository)
            )
            val uiState by viewModel.uiState.collectAsState()

            SignupScreen(
                uiState = uiState,
                onFullNameChange = viewModel::onFullNameChange,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                onSignupClick = {
                    viewModel.onSignupClick {
                        // Navigate to dashboard on successful signup
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

        // Add Credential Screen
        composable(VaultenDestinations.ADD_CREDENTIAL) { backStackEntry ->
            val viewModel: CreateEditCredentialViewModel = viewModel(
                factory = CreateEditCredentialViewModelFactory(
                    createCredentialUseCase,
                    updateCredentialUseCase
                )
            )
            val uiState by viewModel.uiState.collectAsState()

            // Check if a password was generated and use it
            val generatedPassword = backStackEntry
                .savedStateHandle
                .getStateFlow<String?>("generatedPassword", null)
                .collectAsState()

            androidx.compose.runtime.LaunchedEffect(generatedPassword.value) {
                generatedPassword.value?.let { password ->
                    viewModel.onPasswordChange(password)
                    // Clear the saved state to avoid re-applying
                    backStackEntry.savedStateHandle.set<String?>("generatedPassword", null)
                }
            }

            CreateEditCredentialScreen(
                uiState = uiState,
                onNameChange = viewModel::onNameChange,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onUrlChange = viewModel::onUrlChange,
                onSaveClick = viewModel::saveCredential,
                onCancelClick = {
                    navController.popBackStack()
                },
                onGeneratePasswordClick = {
                    navController.navigate(VaultenDestinations.PASSWORD_GENERATOR_FOR_CREDENTIAL)
                }
            )

            // Navigate back when saved
            if (uiState.savedSuccessfully) {
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(500)
                    navController.popBackStack()
                }
            }
        }

        // Edit Credential Screen
        composable(VaultenDestinations.EDIT_CREDENTIAL) { backStackEntry ->
            val credentialId = backStackEntry.arguments?.getString("credentialId") ?: return@composable
            val viewModel: CreateEditCredentialViewModel = viewModel(
                factory = CreateEditCredentialViewModelFactory(
                    createCredentialUseCase,
                    updateCredentialUseCase,
                    getCredentialByIdUseCase
                )
            )
            val uiState by viewModel.uiState.collectAsState()

            // Load credential on first composition only
            androidx.compose.runtime.LaunchedEffect(Unit) {
                viewModel.loadCredentialForEditing(credentialId)
            }

            // Check if a password was generated and use it - this must run after loading
            val generatedPassword = backStackEntry
                .savedStateHandle
                .getStateFlow<String?>("generatedPassword", null)
                .collectAsState()

            // Apply generated password with a small delay to ensure it runs after loading
            androidx.compose.runtime.LaunchedEffect(generatedPassword.value) {
                if (generatedPassword.value != null) {
                    // Small delay to let the credential load first
                    kotlinx.coroutines.delay(50)
                    viewModel.onPasswordChange(generatedPassword.value!!)
                    // Clear the saved state to avoid re-applying
                    backStackEntry.savedStateHandle.set<String?>("generatedPassword", null)
                }
            }

            CreateEditCredentialScreen(
                uiState = uiState,
                onNameChange = viewModel::onNameChange,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onUrlChange = viewModel::onUrlChange,
                onSaveClick = viewModel::saveCredential,
                onCancelClick = {
                    navController.popBackStack()
                },
                onGeneratePasswordClick = {
                    navController.navigate(VaultenDestinations.PASSWORD_GENERATOR_FOR_CREDENTIAL)
                }
            )

            // Navigate back when saved
            if (uiState.savedSuccessfully) {
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(500)
                    navController.popBackStack()
                }
            }
        }

        // Password Generator Screen (standalone)
        composable(VaultenDestinations.PASSWORD_GENERATOR) {
            val viewModel: PasswordGeneratorViewModel = viewModel(
                factory = PasswordGeneratorViewModelFactory(generatePasswordUseCase)
            )
            val uiState by viewModel.uiState.collectAsState()

            PasswordGeneratorScreen(
                uiState = uiState,
                onGenerateClick = viewModel::generatePassword,
                onLengthChange = viewModel::setLength,
                onUppercaseToggle = viewModel::toggleUppercase,
                onLowercaseToggle = viewModel::toggleLowercase,
                onNumbersToggle = viewModel::toggleNumbers,
                onSymbolsToggle = viewModel::toggleSymbols,
                onCopyClick = viewModel::markPasswordAsCopied,
                onBackClick = {
                    navController.popBackStack()
                },
                showUseButton = false
            )

            // Clear copied feedback after 2 seconds
            if (uiState.copiedPassword) {
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    viewModel.clearCopiedFeedback()
                }
            }
        }

        // Password Generator Screen (for credential form)
        composable(VaultenDestinations.PASSWORD_GENERATOR_FOR_CREDENTIAL) {
            val viewModel: PasswordGeneratorViewModel = viewModel(
                factory = PasswordGeneratorViewModelFactory(generatePasswordUseCase)
            )
            val uiState by viewModel.uiState.collectAsState()

            PasswordGeneratorScreen(
                uiState = uiState,
                onGenerateClick = viewModel::generatePassword,
                onLengthChange = viewModel::setLength,
                onUppercaseToggle = viewModel::toggleUppercase,
                onLowercaseToggle = viewModel::toggleLowercase,
                onNumbersToggle = viewModel::toggleNumbers,
                onSymbolsToggle = viewModel::toggleSymbols,
                onCopyClick = viewModel::markPasswordAsCopied,
                onBackClick = {
                    navController.popBackStack()
                },
                onUsePasswordClick = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "generatedPassword",
                        uiState.generatedPassword
                    )
                    navController.popBackStack()
                },
                showUseButton = true
            )

            // Clear copied feedback after 2 seconds
            if (uiState.copiedPassword) {
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    viewModel.clearCopiedFeedback()
                }
            }
        }

        // TODO: Add other screen destinations as they are implemented
        // - Security Settings
    }
}
