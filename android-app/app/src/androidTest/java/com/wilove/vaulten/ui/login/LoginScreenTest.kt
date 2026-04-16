package com.wilove.vaulten.ui.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wilove.vaulten.ui.theme.VaultenTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loginScreen_showsCoreElements() {
        composeRule.setContent {
            VaultenTheme {
                LoginScreen(
                    uiState = LoginUiState(),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onUnlockClick = {},
                    onBiometricLoginSuccess = {},
                    onSignupClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(LoginTestTags.Title).assertIsDisplayed()
        composeRule.onNodeWithTag(LoginTestTags.EmailField).assertIsDisplayed()
        composeRule.onNodeWithTag(LoginTestTags.PasswordField).assertIsDisplayed()
        composeRule.onNodeWithTag(LoginTestTags.UnlockButton).assertIsDisplayed().assertIsEnabled()
        composeRule.onNodeWithTag(LoginTestTags.SignupButton).assertIsDisplayed().assertIsEnabled()
        composeRule.onNodeWithTag(LoginTestTags.ErrorText).assertDoesNotExist()
        composeRule.onNodeWithTag(LoginTestTags.Loading).assertDoesNotExist()
        composeRule.onNodeWithTag(LoginTestTags.BiometricButton).assertDoesNotExist()
    }

    @Test
    fun loginScreen_showsBiometricButton_whenCredentialsAvailable() {
        composeRule.setContent {
            VaultenTheme {
                LoginScreen(
                    uiState = LoginUiState(hasBiometricCredentials = true),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onUnlockClick = {},
                    onBiometricLoginSuccess = {},
                    onSignupClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(LoginTestTags.BiometricButton).assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsErrorMessage() {
        val errorMessage = "Invalid master password"
        composeRule.setContent {
            VaultenTheme {
                LoginScreen(
                    uiState = LoginUiState(errorMessage = errorMessage),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onUnlockClick = {},
                    onBiometricLoginSuccess = {},
                    onSignupClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(LoginTestTags.ErrorText).assertIsDisplayed()
        composeRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsLoadingAndDisablesUnlock() {
        composeRule.setContent {
            VaultenTheme {
                LoginScreen(
                    uiState = LoginUiState(isLoading = true),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onUnlockClick = {},
                    onBiometricLoginSuccess = {},
                    onSignupClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(LoginTestTags.Loading).assertIsDisplayed()
        composeRule.onNodeWithTag(LoginTestTags.UnlockButton).assertIsNotEnabled()
    }

    @Test
    fun loginScreen_lockedOut_disablesUnlockButton() {
        composeRule.setContent {
            VaultenTheme {
                LoginScreen(
                    uiState = LoginUiState(isLockedOut = true, remainingAttempts = 0),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onUnlockClick = {},
                    onBiometricLoginSuccess = {},
                    onSignupClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(LoginTestTags.UnlockButton).assertIsNotEnabled()
    }
}
