package com.wilove.vaulten.ui.passwordgenerator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class PasswordGeneratorScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun passwordGeneratorScreen_displaysPassword() {
        composeTestRule.setContent {
            PasswordGeneratorScreen(
                uiState = PasswordGeneratorUiState(
                    generatedPassword = "aB3$xKp9mQ2@wL7nR1!vT",
                    length = 16
                ),
                onGenerateClick = {},
                onLengthChange = {},
                onUppercaseToggle = {},
                onLowercaseToggle = {},
                onNumbersToggle = {},
                onSymbolsToggle = {},
                onCopyClick = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithTag(PasswordGeneratorTestTags.PasswordText)
            .assertIsDisplayed()
    }

    @Test
    fun passwordGeneratorScreen_displaysLengthSlider() {
        composeTestRule.setContent {
            PasswordGeneratorScreen(
                uiState = PasswordGeneratorUiState(),
                onGenerateClick = {},
                onLengthChange = {},
                onUppercaseToggle = {},
                onLowercaseToggle = {},
                onNumbersToggle = {},
                onSymbolsToggle = {},
                onCopyClick = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithTag(PasswordGeneratorTestTags.LengthSlider)
            .assertIsDisplayed()
    }

    @Test
    fun passwordGeneratorScreen_displaysCharacterOptions() {
        composeTestRule.setContent {
            PasswordGeneratorScreen(
                uiState = PasswordGeneratorUiState(),
                onGenerateClick = {},
                onLengthChange = {},
                onUppercaseToggle = {},
                onLowercaseToggle = {},
                onNumbersToggle = {},
                onSymbolsToggle = {},
                onCopyClick = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithTag(PasswordGeneratorTestTags.UppercaseCheckbox)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(PasswordGeneratorTestTags.LowercaseCheckbox)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(PasswordGeneratorTestTags.NumbersCheckbox)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(PasswordGeneratorTestTags.SymbolsCheckbox)
            .assertIsDisplayed()
    }

    @Test
    fun passwordGeneratorScreen_generateButtonClickable() {
        var generateClicked = false
        composeTestRule.setContent {
            PasswordGeneratorScreen(
                uiState = PasswordGeneratorUiState(),
                onGenerateClick = { generateClicked = true },
                onLengthChange = {},
                onUppercaseToggle = {},
                onLowercaseToggle = {},
                onNumbersToggle = {},
                onSymbolsToggle = {},
                onCopyClick = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithTag(PasswordGeneratorTestTags.GenerateButton)
            .performClick()
    }

    @Test
    fun passwordGeneratorScreen_copyButtonClickable() {
        var copyClicked = false
        composeTestRule.setContent {
            PasswordGeneratorScreen(
                uiState = PasswordGeneratorUiState(
                    generatedPassword = "testPassword123"
                ),
                onGenerateClick = {},
                onLengthChange = {},
                onUppercaseToggle = {},
                onLowercaseToggle = {},
                onNumbersToggle = {},
                onSymbolsToggle = {},
                onCopyClick = { copyClicked = true },
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithTag(PasswordGeneratorTestTags.CopyButton)
            .performClick()
    }

    @Test
    fun passwordGeneratorScreen_showsCopiedIndicator() {
        composeTestRule.setContent {
            PasswordGeneratorScreen(
                uiState = PasswordGeneratorUiState(
                    generatedPassword = "testPassword123",
                    copiedPassword = true
                ),
                onGenerateClick = {},
                onLengthChange = {},
                onUppercaseToggle = {},
                onLowercaseToggle = {},
                onNumbersToggle = {},
                onSymbolsToggle = {},
                onCopyClick = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithTag(PasswordGeneratorTestTags.CopiedIndicator)
            .assertIsDisplayed()
    }

    @Test
    fun passwordGeneratorScreen_uppercaseCheckboxClickable() {
        var uppercaseToggled = false
        composeTestRule.setContent {
            PasswordGeneratorScreen(
                uiState = PasswordGeneratorUiState(),
                onGenerateClick = {},
                onLengthChange = {},
                onUppercaseToggle = { uppercaseToggled = true },
                onLowercaseToggle = {},
                onNumbersToggle = {},
                onSymbolsToggle = {},
                onCopyClick = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithTag(PasswordGeneratorTestTags.UppercaseCheckbox)
            .performClick()
    }

    @Test
    fun passwordGeneratorScreen_showsErrorMessage() {
        composeTestRule.setContent {
            PasswordGeneratorScreen(
                uiState = PasswordGeneratorUiState(
                    errorMessage = "Error generating password"
                ),
                onGenerateClick = {},
                onLengthChange = {},
                onUppercaseToggle = {},
                onLowercaseToggle = {},
                onNumbersToggle = {},
                onSymbolsToggle = {},
                onCopyClick = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithTag(PasswordGeneratorTestTags.ErrorMessage)
            .assertIsDisplayed()
    }

    @Test
    fun passwordGeneratorScreen_displaysLengthValue() {
        composeTestRule.setContent {
            PasswordGeneratorScreen(
                uiState = PasswordGeneratorUiState(length = 24),
                onGenerateClick = {},
                onLengthChange = {},
                onUppercaseToggle = {},
                onLowercaseToggle = {},
                onNumbersToggle = {},
                onSymbolsToggle = {},
                onCopyClick = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithTag(PasswordGeneratorTestTags.LengthDisplay)
            .assertIsDisplayed()
    }
}
