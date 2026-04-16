package com.wilove.vaulten.ui.trash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.ui.theme.VaultenTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrashScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun trash_showsLoadingState() {
        composeRule.setContent {
            VaultenTheme {
                TrashScreen(
                    uiState = TrashUiState(isLoading = true),
                    onBackClick = {},
                    onRestore = {},
                    onPermanentlyDelete = {}
                )
            }
        }

        composeRule.onNodeWithTag(TrashTestTags.Loading).assertIsDisplayed()
    }

    @Test
    fun trash_showsErrorState() {
        composeRule.setContent {
            VaultenTheme {
                TrashScreen(
                    uiState = TrashUiState(isLoading = false, errorMessage = "Error"),
                    onBackClick = {},
                    onRestore = {},
                    onPermanentlyDelete = {}
                )
            }
        }

        composeRule.onNodeWithTag(TrashTestTags.ErrorText).assertIsDisplayed()
    }

    @Test
    fun trash_showsEmptyState() {
        composeRule.setContent {
            VaultenTheme {
                TrashScreen(
                    uiState = TrashUiState(isLoading = false, credentials = emptyList()),
                    onBackClick = {},
                    onRestore = {},
                    onPermanentlyDelete = {}
                )
            }
        }

        composeRule.onNodeWithTag(TrashTestTags.EmptyText).assertIsDisplayed()
    }

    @Test
    fun trash_showsList_whenCredentialsPresent() {
        val credentials = listOf(
            Credential("1", "Gmail", "user@gmail.com", "pass", "https://gmail.com")
        )
        composeRule.setContent {
            VaultenTheme {
                TrashScreen(
                    uiState = TrashUiState(isLoading = false, credentials = credentials),
                    onBackClick = {},
                    onRestore = {},
                    onPermanentlyDelete = {}
                )
            }
        }

        composeRule.onNodeWithTag(TrashTestTags.List).assertIsDisplayed()
    }
}
