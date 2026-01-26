package com.wilove.vaulten.ui.credentials

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.wilove.vaulten.domain.model.Credential
import org.junit.Rule
import org.junit.Test

class CredentialDetailScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockCredential = Credential(
        id = "1",
        name = "Gmail",
        username = "user@gmail.com",
        password = "password123",
        url = "https://gmail.com",
        lastModified = System.currentTimeMillis()
    )

    @Test
    fun credentialDetailScreen_displaysCredentialInfo() {
        composeTestRule.setContent {
            CredentialDetailScreen(
                uiState = CredentialDetailUiState(credential = mockCredential),
                onBackClick = {},
                onEditClick = {},
                onDeleteClick = {},
                onCopyField = { _, _ -> },
                onTogglePasswordVisibility = {}
            )
        }

        composeTestRule.onNodeWithTag(CredentialDetailTestTags.NameField)
            .assertIsDisplayed()
    }

    @Test
    fun credentialDetailScreen_showsLoadingState() {
        composeTestRule.setContent {
            CredentialDetailScreen(
                uiState = CredentialDetailUiState(isLoading = true),
                onBackClick = {},
                onEditClick = {},
                onDeleteClick = {},
                onCopyField = { _, _ -> },
                onTogglePasswordVisibility = {}
            )
        }

        composeTestRule.onNodeWithTag(CredentialDetailTestTags.Loading)
            .assertIsDisplayed()
    }

    @Test
    fun credentialDetailScreen_showsErrorState() {
        composeTestRule.setContent {
            CredentialDetailScreen(
                uiState = CredentialDetailUiState(errorMessage = "Failed to load"),
                onBackClick = {},
                onEditClick = {},
                onDeleteClick = {},
                onCopyField = { _, _ -> },
                onTogglePasswordVisibility = {}
            )
        }

        composeTestRule.onNodeWithTag(CredentialDetailTestTags.ErrorMessage)
            .assertIsDisplayed()
    }

    @Test
    fun credentialDetailScreen_passwordToggleWorks() {
        var passwordVisible = false
        composeTestRule.setContent {
            CredentialDetailScreen(
                uiState = CredentialDetailUiState(
                    credential = mockCredential,
                    passwordVisible = passwordVisible
                ),
                onBackClick = {},
                onEditClick = {},
                onDeleteClick = {},
                onCopyField = { _, _ -> },
                onTogglePasswordVisibility = { passwordVisible = !passwordVisible }
            )
        }

        composeTestRule.onNodeWithTag(CredentialDetailTestTags.PasswordToggle)
            .performClick()
    }

    @Test
    fun credentialDetailScreen_editButtonClickable() {
        var editClicked = false
        composeTestRule.setContent {
            CredentialDetailScreen(
                uiState = CredentialDetailUiState(credential = mockCredential),
                onBackClick = {},
                onEditClick = { editClicked = true },
                onDeleteClick = {},
                onCopyField = { _, _ -> },
                onTogglePasswordVisibility = {}
            )
        }

        composeTestRule.onNodeWithTag(CredentialDetailTestTags.EditButton)
            .performClick()
    }

    @Test
    fun credentialDetailScreen_copyFieldWorks() {
        var copiedField = ""
        composeTestRule.setContent {
            CredentialDetailScreen(
                uiState = CredentialDetailUiState(credential = mockCredential),
                onBackClick = {},
                onEditClick = {},
                onDeleteClick = {},
                onCopyField = { field, _ -> copiedField = field },
                onTogglePasswordVisibility = {}
            )
        }

        composeTestRule.onNodeWithTag("${CredentialDetailTestTags.UsernameField}CopyButton")
            .performClick()
    }
}
