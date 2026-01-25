package com.wilove.vaulten.ui.signup

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
 * Instrumented tests for [SignupScreen].
 * These tests run on an Android device or emulator.
 */
@RunWith(AndroidJUnit4::class)
class SignupScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    /** Verifies the default UI renders all core elements. */
    fun signupScreen_showsCoreElements() {
        composeRule.setContent {
            VaultenTheme {
                SignupScreen(
                    uiState = SignupUiState(),
                    onFullNameChange = {},
                    onEmailChange = {},
                    onPasswordChange = {},
                    onConfirmPasswordChange = {},
                    onSignupClick = {},
                    onLoginClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(SignupTestTags.Title).assertIsDisplayed()
        composeRule.onNodeWithTag(SignupTestTags.FullNameField).assertIsDisplayed()
        composeRule.onNodeWithTag(SignupTestTags.EmailField).assertIsDisplayed()
        composeRule.onNodeWithTag(SignupTestTags.PasswordField).assertIsDisplayed()
        composeRule.onNodeWithTag(SignupTestTags.ConfirmPasswordField).assertIsDisplayed()
        composeRule.onNodeWithTag(SignupTestTags.SignupButton).assertIsDisplayed().assertIsEnabled()
        composeRule.onNodeWithTag(SignupTestTags.LoginButton).assertIsDisplayed().assertIsEnabled()
        composeRule.onNodeWithTag(SignupTestTags.ErrorText).assertDoesNotExist()
        composeRule.onNodeWithTag(SignupTestTags.Loading).assertDoesNotExist()
    }

    @Test
    /** Verifies error text appears when provided by state. */
    fun signupScreen_showsErrorMessage() {
        val errorMessage = "Passwords do not match"
        composeRule.setContent {
            VaultenTheme {
                SignupScreen(
                    uiState = SignupUiState(errorMessage = errorMessage),
                    onFullNameChange = {},
                    onEmailChange = {},
                    onPasswordChange = {},
                    onConfirmPasswordChange = {},
                    onSignupClick = {},
                    onLoginClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(SignupTestTags.ErrorText).assertIsDisplayed()
        composeRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    /** Verifies loading state shows spinner and disables primary action. */
    fun signupScreen_showsLoadingAndDisablesPrimaryAction() {
        composeRule.setContent {
            VaultenTheme {
                SignupScreen(
                    uiState = SignupUiState(isLoading = true),
                    onFullNameChange = {},
                    onEmailChange = {},
                    onPasswordChange = {},
                    onConfirmPasswordChange = {},
                    onSignupClick = {},
                    onLoginClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(SignupTestTags.Loading).assertIsDisplayed()
        composeRule.onNodeWithTag(SignupTestTags.SignupButton).assertIsNotEnabled()
    }
}
