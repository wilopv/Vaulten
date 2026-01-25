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

/**
 * Instrumented tests for [LoginScreen].
 * These tests run on an Android device or emulator.
 */
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    /** Verifies the default UI renders all core elements. */
    fun loginScreen_showsCoreElements() {
        composeRule.setContent {
            VaultenTheme {
                LoginScreen(
                    uiState = LoginUiState(),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onUnlockClick = {},
                    onBiometricToggle = {},
                    onSignupClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(LoginTestTags.Title).assertIsDisplayed()
        composeRule.onNodeWithTag(LoginTestTags.EmailField).assertIsDisplayed()
        composeRule.onNodeWithTag(LoginTestTags.PasswordField).assertIsDisplayed()
        composeRule.onNodeWithTag(LoginTestTags.UnlockButton).assertIsDisplayed().assertIsEnabled()
        composeRule.onNodeWithTag(LoginTestTags.SignupButton).assertIsDisplayed().assertIsEnabled()
        composeRule.onNodeWithTag(LoginTestTags.BiometricToggle).assertIsDisplayed()
        composeRule.onNodeWithTag(LoginTestTags.ErrorText).assertDoesNotExist()
        composeRule.onNodeWithTag(LoginTestTags.Loading).assertDoesNotExist()
        composeRule.onNodeWithTag(LoginTestTags.AttemptsText).assertIsDisplayed()
    }

    @Test
    /** Verifies error text appears when provided by state. */
    fun loginScreen_showsErrorMessage() {
        val errorMessage = "Invalid master password"
        composeRule.setContent {
            VaultenTheme {
                LoginScreen(
                    uiState = LoginUiState(errorMessage = errorMessage),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onUnlockClick = {},
                    onBiometricToggle = {},
                    onSignupClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(LoginTestTags.ErrorText).assertIsDisplayed()
        composeRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    /** Verifies loading state shows spinner and disables primary action. */
    fun loginScreen_showsLoadingAndDisablesUnlock() {
        composeRule.setContent {
            VaultenTheme {
                LoginScreen(
                    uiState = LoginUiState(isLoading = true),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onUnlockClick = {},
                    onBiometricToggle = {},
                    onSignupClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(LoginTestTags.Loading).assertIsDisplayed()
        composeRule.onNodeWithTag(LoginTestTags.UnlockButton).assertIsNotEnabled()
    }

    @Test
    /** Verifies lockout state disables unlock and shows lockout message. */
    fun loginScreen_showsLockoutState() {
        composeRule.setContent {
            VaultenTheme {
                LoginScreen(
                    uiState = LoginUiState(isLockedOut = true, remainingAttempts = 0),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onUnlockClick = {},
                    onBiometricToggle = {},
                    onSignupClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(LoginTestTags.LockoutText).assertIsDisplayed()
        composeRule.onNodeWithTag(LoginTestTags.UnlockButton).assertIsNotEnabled()
    }
}
