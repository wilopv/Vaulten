package com.wilove.vaulten.ui.credentials

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.ui.theme.VaultenTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for [CredentialsListScreen].
 * These tests run on an Android device or emulator.
 */
@RunWith(AndroidJUnit4::class)
class CredentialsListScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun credentialsList_showsCoreElements() {
        val credentials = listOf(
            Credential("1", "Gmail", "user@gmail.com", "pass", "https://gmail.com")
        )
        composeRule.setContent {
            VaultenTheme {
                CredentialsListScreen(
                    uiState = CredentialsListUiState(credentials = credentials),
                    onSearchQueryChange = {},
                    onCredentialClick = {},
                    onAddCredentialClick = {},
                    onRefresh = {}
                )
            }
        }

        composeRule.onNodeWithTag(CredentialsListTestTags.Title).assertIsDisplayed()
        composeRule.onNodeWithTag(CredentialsListTestTags.SearchField).assertIsDisplayed()
        composeRule.onNodeWithTag(CredentialsListTestTags.AddButton).assertIsDisplayed().assertIsEnabled()
        composeRule.onNodeWithTag(CredentialsListTestTags.List).assertIsDisplayed()
    }

    @Test
    fun credentialsList_showsLoadingState() {
        composeRule.setContent {
            VaultenTheme {
                CredentialsListScreen(
                    uiState = CredentialsListUiState(isLoading = true),
                    onSearchQueryChange = {},
                    onCredentialClick = {},
                    onAddCredentialClick = {},
                    onRefresh = {}
                )
            }
        }

        composeRule.onNodeWithTag(CredentialsListTestTags.Loading).assertIsDisplayed()
    }

    @Test
    fun credentialsList_showsErrorState() {
        composeRule.setContent {
            VaultenTheme {
                CredentialsListScreen(
                    uiState = CredentialsListUiState(errorMessage = "Oops"),
                    onSearchQueryChange = {},
                    onCredentialClick = {},
                    onAddCredentialClick = {},
                    onRefresh = {}
                )
            }
        }

        composeRule.onNodeWithTag(CredentialsListTestTags.ErrorText).assertIsDisplayed()
        composeRule.onNodeWithTag(CredentialsListTestTags.RetryButton).assertIsDisplayed()
    }

    @Test
    fun credentialsList_showsEmptyState() {
        composeRule.setContent {
            VaultenTheme {
                CredentialsListScreen(
                    uiState = CredentialsListUiState(credentials = emptyList()),
                    onSearchQueryChange = {},
                    onCredentialClick = {},
                    onAddCredentialClick = {},
                    onRefresh = {}
                )
            }
        }

        composeRule.onNodeWithTag(CredentialsListTestTags.EmptyText).assertIsDisplayed()
    }
}
