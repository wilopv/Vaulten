package com.wilove.vaulten.ui.credentials

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class CreateEditCredentialScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun createEditCredentialScreen_displaysCreateMode() {
        composeTestRule.setContent {
            CreateEditCredentialScreen(
                uiState = CreateEditCredentialUiState(isEditMode = false),
                onNameChange = {},
                onUsernameChange = {},
                onPasswordChange = {},
                onUrlChange = {},
                onSaveClick = {},
                onCancelClick = {}
            )
        }

        composeTestRule.onNodeWithTag(CreateEditCredentialTestTags.NameField)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(CreateEditCredentialTestTags.SaveButton)
            .assertIsDisplayed()
    }

    @Test
    fun createEditCredentialScreen_displaysEditMode() {
        composeTestRule.setContent {
            CreateEditCredentialScreen(
                uiState = CreateEditCredentialUiState(
                    isEditMode = true,
                    name = "Gmail",
                    username = "user@gmail.com",
                    password = "password123",
                    url = "https://gmail.com"
                ),
                onNameChange = {},
                onUsernameChange = {},
                onPasswordChange = {},
                onUrlChange = {},
                onSaveClick = {},
                onCancelClick = {}
            )
        }

        composeTestRule.onNodeWithTag(CreateEditCredentialTestTags.NameField)
            .assertIsDisplayed()
    }

    @Test
    fun createEditCredentialScreen_showsLoadingState() {
        composeTestRule.setContent {
            CreateEditCredentialScreen(
                uiState = CreateEditCredentialUiState(isLoading = true),
                onNameChange = {},
                onUsernameChange = {},
                onPasswordChange = {},
                onUrlChange = {},
                onSaveClick = {},
                onCancelClick = {}
            )
        }

        composeTestRule.onNodeWithTag(CreateEditCredentialTestTags.Loading)
            .assertIsDisplayed()
    }

    @Test
    fun createEditCredentialScreen_showsSuccessState() {
        composeTestRule.setContent {
            CreateEditCredentialScreen(
                uiState = CreateEditCredentialUiState(savedSuccessfully = true),
                onNameChange = {},
                onUsernameChange = {},
                onPasswordChange = {},
                onUrlChange = {},
                onSaveClick = {},
                onCancelClick = {}
            )
        }

        composeTestRule.onNodeWithTag(CreateEditCredentialTestTags.SuccessMessage)
            .assertIsDisplayed()
    }

    @Test
    fun createEditCredentialScreen_showsErrorMessage() {
        composeTestRule.setContent {
            CreateEditCredentialScreen(
                uiState = CreateEditCredentialUiState(errorMessage = "Failed to save"),
                onNameChange = {},
                onUsernameChange = {},
                onPasswordChange = {},
                onUrlChange = {},
                onSaveClick = {},
                onCancelClick = {}
            )
        }

        composeTestRule.onNodeWithTag(CreateEditCredentialTestTags.ErrorMessage)
            .assertIsDisplayed()
    }

    @Test
    fun createEditCredentialScreen_nameFieldInput() {
        var nameInput = ""
        composeTestRule.setContent {
            CreateEditCredentialScreen(
                uiState = CreateEditCredentialUiState(name = nameInput),
                onNameChange = { nameInput = it },
                onUsernameChange = {},
                onPasswordChange = {},
                onUrlChange = {},
                onSaveClick = {},
                onCancelClick = {}
            )
        }

        composeTestRule.onNodeWithTag(CreateEditCredentialTestTags.NameField)
            .performTextInput("Gmail")
    }

    @Test
    fun createEditCredentialScreen_saveButtonClickable() {
        var saveClicked = false
        composeTestRule.setContent {
            CreateEditCredentialScreen(
                uiState = CreateEditCredentialUiState(),
                onNameChange = {},
                onUsernameChange = {},
                onPasswordChange = {},
                onUrlChange = {},
                onSaveClick = { saveClicked = true },
                onCancelClick = {}
            )
        }

        composeTestRule.onNodeWithTag(CreateEditCredentialTestTags.SaveButton)
            .performClick()
    }

    @Test
    fun createEditCredentialScreen_cancelButtonClickable() {
        var cancelClicked = false
        composeTestRule.setContent {
            CreateEditCredentialScreen(
                uiState = CreateEditCredentialUiState(),
                onNameChange = {},
                onUsernameChange = {},
                onPasswordChange = {},
                onUrlChange = {},
                onSaveClick = {},
                onCancelClick = { cancelClicked = true }
            )
        }

        composeTestRule.onNodeWithTag(CreateEditCredentialTestTags.CancelButton)
            .performClick()
    }

    @Test
    fun createEditCredentialScreen_showsValidationError() {
        composeTestRule.setContent {
            CreateEditCredentialScreen(
                uiState = CreateEditCredentialUiState(
                    nameError = "Name is required"
                ),
                onNameChange = {},
                onUsernameChange = {},
                onPasswordChange = {},
                onUrlChange = {},
                onSaveClick = {},
                onCancelClick = {}
            )
        }

        composeTestRule.onNodeWithTag(CreateEditCredentialTestTags.NameField)
            .assertIsDisplayed()
    }
}
