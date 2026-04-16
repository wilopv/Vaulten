package com.wilove.vaulten.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wilove.vaulten.data.local.SessionManager
import com.wilove.vaulten.data.local.TokenManager
import com.wilove.vaulten.ui.credentials.AppPickerScreen
import com.wilove.vaulten.ui.credentials.AppPickerViewModel
import com.wilove.vaulten.ui.credentials.CreateEditCredentialScreen
import com.wilove.vaulten.ui.credentials.CreateEditCredentialViewModel
import com.wilove.vaulten.ui.credentials.CredentialDetailScreen
import com.wilove.vaulten.ui.credentials.CredentialDetailViewModel
import com.wilove.vaulten.ui.credentials.CredentialsListScreen
import com.wilove.vaulten.ui.credentials.CredentialsListViewModel
import com.wilove.vaulten.ui.dashboard.DashboardScreen
import com.wilove.vaulten.ui.dashboard.DashboardViewModel
import com.wilove.vaulten.ui.lock.LockScreen
import com.wilove.vaulten.ui.lock.LockViewModel
import com.wilove.vaulten.ui.login.LoginScreen
import com.wilove.vaulten.ui.login.LoginViewModel
import com.wilove.vaulten.ui.passwordgenerator.PasswordGeneratorScreen
import com.wilove.vaulten.ui.passwordgenerator.PasswordGeneratorViewModel
import com.wilove.vaulten.ui.settings.ChangePasswordScreen
import com.wilove.vaulten.ui.settings.ChangePasswordViewModel
import com.wilove.vaulten.ui.settings.ExportImportScreen
import com.wilove.vaulten.ui.settings.ExportImportViewModel
import com.wilove.vaulten.ui.settings.SettingsScreen
import com.wilove.vaulten.ui.settings.SettingsViewModel
import com.wilove.vaulten.ui.signup.SignupScreen
import com.wilove.vaulten.ui.signup.SignupViewModel
import com.wilove.vaulten.ui.trash.TrashScreen
import com.wilove.vaulten.ui.trash.TrashViewModel

@Composable
fun VaultenNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = VaultenDestinations.LOGIN,
    initialSearchQuery: String? = null
) {
    val context = LocalContext.current

    // TokenManager used only for start destination routing (read-only at startup)
    val tokenManager = androidx.compose.runtime.remember { TokenManager(context) }

    val effectiveStartDestination = androidx.compose.runtime.remember {
        when {
            startDestination != VaultenDestinations.LOGIN -> startDestination
            tokenManager.getToken() == null -> VaultenDestinations.LOGIN
            SessionManager.isLockRequired(tokenManager.getLastActiveTimestamp()) ->
                VaultenDestinations.LOGIN
            else -> VaultenDestinations.LOCK
        }
    }

    NavHost(
        navController = navController,
        startDestination = effectiveStartDestination,
        modifier = modifier
    ) {
        // Login Screen
        composable(VaultenDestinations.LOGIN) {
            val viewModel: LoginViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            val navigateToDashboard = {
                navController.navigate(VaultenDestinations.DASHBOARD) {
                    popUpTo(VaultenDestinations.LOGIN) { inclusive = true }
                }
            }

            LoginScreen(
                uiState = uiState,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onUnlockClick = { viewModel.onUnlockClick(navigateToDashboard) },
                onBiometricLoginSuccess = { viewModel.onBiometricLoginSuccess(navigateToDashboard) },
                onSignupClick = { navController.navigate(VaultenDestinations.SIGNUP) }
            )
        }

        // Signup Screen
        composable(VaultenDestinations.SIGNUP) {
            val viewModel: SignupViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            SignupScreen(
                uiState = uiState,
                onFullNameChange = viewModel::onFullNameChange,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                onSignupClick = {
                    viewModel.onSignupClick {
                        navController.navigate(VaultenDestinations.DASHBOARD) {
                            popUpTo(VaultenDestinations.LOGIN) { inclusive = true }
                        }
                    }
                },
                onLoginClick = { navController.popBackStack() }
            )
        }

        // Dashboard Screen
        composable(VaultenDestinations.DASHBOARD) {
            val viewModel: DashboardViewModel = hiltViewModel()
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
                onRefresh = viewModel::refreshDashboard,
                onLogoutClick = {
                    navController.navigate(VaultenDestinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSettingsClick = {
                    navController.navigate(VaultenDestinations.SETTINGS)
                }
            )
        }

        // Settings Screen
        composable(VaultenDestinations.SETTINGS) {
            val viewModel: SettingsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            SettingsScreen(
                uiState = uiState,
                onBackClick = { navController.popBackStack() },
                onEnableAutofillClick = {},
                onCheckStatus = viewModel::checkAutofillStatus,
                onChangePasswordClick = { navController.navigate(VaultenDestinations.CHANGE_PASSWORD) },
                onExportImportClick = { navController.navigate(VaultenDestinations.EXPORT_IMPORT) }
            )
        }

        // Credentials List Screen
        composable(VaultenDestinations.CREDENTIALS_LIST) {
            val viewModel: CredentialsListViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            androidx.compose.runtime.LaunchedEffect(initialSearchQuery) {
                if (!initialSearchQuery.isNullOrEmpty()) {
                    viewModel.onSearchQueryChange(initialSearchQuery)
                }
            }

            CredentialsListScreen(
                uiState = uiState,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onCredentialClick = { credentialId ->
                    navController.navigate(VaultenDestinations.credentialDetail(credentialId))
                },
                onAddCredentialClick = { navController.navigate(VaultenDestinations.ADD_CREDENTIAL) },
                onBackClick = { navController.popBackStack() },
                onRefresh = viewModel::refresh,
                onTrashClick = { navController.navigate(VaultenDestinations.TRASH) },
                onApplyFilter = viewModel::applyFilter,
                onClearFilter = viewModel::clearFilter
            )
        }

        // Credential Detail Screen
        composable(VaultenDestinations.CREDENTIAL_DETAIL) { backStackEntry ->
            val credentialId = backStackEntry.arguments?.getString("credentialId") ?: return@composable
            val viewModel: CredentialDetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            val navigateBack by viewModel.navigateBack.collectAsState()

            androidx.compose.runtime.LaunchedEffect(credentialId) {
                viewModel.loadCredential(credentialId)
            }
            androidx.compose.runtime.LaunchedEffect(navigateBack) {
                if (navigateBack) navController.popBackStack()
            }

            CredentialDetailScreen(
                uiState = uiState,
                onBackClick = { navController.popBackStack() },
                onEditClick = { navController.navigate(VaultenDestinations.editCredential(credentialId)) },
                onDeleteClick = { viewModel.deleteCredential(credentialId) },
                onCopyField = { fieldName, _ -> viewModel.markFieldAsCopied(fieldName) },
                onTogglePasswordVisibility = viewModel::togglePasswordVisibility
            )
        }

        // Add Credential Screen
        composable(VaultenDestinations.ADD_CREDENTIAL) { backStackEntry ->
            val viewModel: CreateEditCredentialViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            val generatedPassword = backStackEntry
                .savedStateHandle.getStateFlow<String?>("generatedPassword", null)
                .collectAsState()

            androidx.compose.runtime.LaunchedEffect(generatedPassword.value) {
                generatedPassword.value?.let { password ->
                    viewModel.onPasswordChange(password)
                    backStackEntry.savedStateHandle["generatedPassword"] = null
                }
            }

            val selectedApp = backStackEntry
                .savedStateHandle.getStateFlow<String?>("selectedApp", null)
                .collectAsState()

            androidx.compose.runtime.LaunchedEffect(selectedApp.value) {
                selectedApp.value?.let { value ->
                    val parts = value.split("|", limit = 2)
                    if (parts.size == 2) viewModel.onAndroidPackageNameChange(parts[0], parts[1])
                    backStackEntry.savedStateHandle["selectedApp"] = null
                }
            }

            CreateEditCredentialScreen(
                uiState = uiState,
                onNameChange = viewModel::onNameChange,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onUrlChange = viewModel::onUrlChange,
                onSaveClick = viewModel::saveCredential,
                onCancelClick = { navController.popBackStack() },
                onGeneratePasswordClick = {
                    navController.navigate(VaultenDestinations.PASSWORD_GENERATOR_FOR_CREDENTIAL)
                },
                onSelectAppClick = { navController.navigate(VaultenDestinations.APP_PICKER) },
                onAndroidPackageNameChange = viewModel::onAndroidPackageNameChange
            )
        }

        // Edit Credential Screen
        composable(VaultenDestinations.EDIT_CREDENTIAL) { backStackEntry ->
            val credentialId = backStackEntry.arguments?.getString("credentialId") ?: return@composable
            val viewModel: CreateEditCredentialViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            androidx.compose.runtime.LaunchedEffect(Unit) {
                viewModel.loadCredentialForEditing(credentialId)
            }

            val generatedPassword = backStackEntry
                .savedStateHandle.getStateFlow<String?>("generatedPassword", null)
                .collectAsState()

            androidx.compose.runtime.LaunchedEffect(generatedPassword.value) {
                if (generatedPassword.value != null) {
                    kotlinx.coroutines.delay(50)
                    viewModel.onPasswordChange(generatedPassword.value!!)
                    backStackEntry.savedStateHandle["generatedPassword"] = null
                }
            }

            val selectedApp = backStackEntry
                .savedStateHandle.getStateFlow<String?>("selectedApp", null)
                .collectAsState()

            androidx.compose.runtime.LaunchedEffect(selectedApp.value) {
                selectedApp.value?.let { value ->
                    val parts = value.split("|", limit = 2)
                    if (parts.size == 2) viewModel.onAndroidPackageNameChange(parts[0], parts[1])
                    backStackEntry.savedStateHandle["selectedApp"] = null
                }
            }

            CreateEditCredentialScreen(
                uiState = uiState,
                onNameChange = viewModel::onNameChange,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onUrlChange = viewModel::onUrlChange,
                onSaveClick = viewModel::saveCredential,
                onCancelClick = { navController.popBackStack() },
                onGeneratePasswordClick = {
                    navController.navigate(VaultenDestinations.PASSWORD_GENERATOR_FOR_CREDENTIAL)
                },
                onSelectAppClick = { navController.navigate(VaultenDestinations.APP_PICKER) },
                onAndroidPackageNameChange = viewModel::onAndroidPackageNameChange
            )
        }

        // Password Generator Screen (standalone)
        composable(VaultenDestinations.PASSWORD_GENERATOR) {
            val viewModel: PasswordGeneratorViewModel = hiltViewModel()
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
                onBackClick = { navController.popBackStack() },
                showUseButton = false
            )

            if (uiState.copiedPassword) {
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    viewModel.clearCopiedFeedback()
                }
            }
        }

        // Password Generator Screen (for credential form)
        composable(VaultenDestinations.PASSWORD_GENERATOR_FOR_CREDENTIAL) {
            val viewModel: PasswordGeneratorViewModel = hiltViewModel()
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
                onBackClick = { navController.popBackStack() },
                onUsePasswordClick = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "generatedPassword", uiState.generatedPassword
                    )
                    navController.popBackStack()
                },
                showUseButton = true
            )

            if (uiState.copiedPassword) {
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    viewModel.clearCopiedFeedback()
                }
            }
        }

        // Change Password Screen
        composable(VaultenDestinations.CHANGE_PASSWORD) {
            val viewModel: ChangePasswordViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            ChangePasswordScreen(
                uiState = uiState,
                onCurrentPasswordChange = viewModel::onCurrentPasswordChange,
                onNewPasswordChange = viewModel::onNewPasswordChange,
                onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                onSaveClick = viewModel::changePassword,
                onBackClick = { navController.popBackStack() },
                onSavedSuccessfully = { navController.popBackStack() }
            )
        }

        // Trash Screen
        composable(VaultenDestinations.TRASH) {
            val viewModel: TrashViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            TrashScreen(
                uiState = uiState,
                onBackClick = { navController.popBackStack() },
                onRestore = viewModel::restore,
                onPermanentlyDelete = viewModel::permanentlyDelete
            )
        }

        // App Picker Screen
        composable(VaultenDestinations.APP_PICKER) {
            val viewModel: AppPickerViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            AppPickerScreen(
                uiState = uiState,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onAppSelected = { packageName, appLabel ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "selectedApp", "$packageName|$appLabel"
                    )
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Export / Import Screen
        composable(VaultenDestinations.EXPORT_IMPORT) {
            val viewModel: ExportImportViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            ExportImportScreen(
                uiState = uiState,
                onExportCsvClick = viewModel::exportAsCsv,
                onRequestEncryptedExportClick = viewModel::requestEncryptedExport,
                onExportEncryptedConfirm = viewModel::exportAsEncryptedJson,
                onExportEncryptedDismiss = viewModel::dismissEncryptionDialog,
                onImportEncryptedConfirm = viewModel::importEncryptedJson,
                onImportEncryptedDismiss = viewModel::dismissDecryptionDialog,
                onFileSelected = viewModel::onFileSelected,
                onCsvExportHandled = viewModel::onCsvExportHandled,
                onJsonExportHandled = viewModel::onJsonExportHandled,
                onBackClick = { navController.popBackStack() },
                onClearMessages = viewModel::clearMessages
            )
        }

        // Lock Screen
        composable(VaultenDestinations.LOCK) {
            val viewModel: LockViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            LockScreen(
                uiState = uiState,
                onUnlockSuccess = {
                    viewModel.onUnlockSuccess()
                    navController.navigate(VaultenDestinations.DASHBOARD) {
                        popUpTo(VaultenDestinations.LOCK) { inclusive = true }
                    }
                },
                onUnlockError = viewModel::onUnlockError,
                onLogout = {
                    tokenManager.deleteToken()
                    navController.navigate(VaultenDestinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
