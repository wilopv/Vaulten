package com.wilove.vaulten.ui.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.model.DashboardData
import com.wilove.vaulten.ui.theme.VaultenTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val sampleData = DashboardData(
        recentCredentials = listOf(
            Credential("1", "Gmail", "user@gmail.com", "pass", "https://gmail.com")
        ),
        securityAlerts = emptyList(),
        totalCredentials = 1
    )

    @Test
    fun dashboard_showsLoadingState() {
        composeRule.setContent {
            VaultenTheme {
                DashboardScreen(
                    uiState = DashboardUiState(isLoading = true),
                    onCredentialClick = {},
                    onAddCredentialClick = {},
                    onViewAllClick = {},
                    onRefresh = {},
                    onLogoutClick = {},
                    onSettingsClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(DashboardTestTags.Loading).assertIsDisplayed()
    }

    @Test
    fun dashboard_showsErrorState() {
        composeRule.setContent {
            VaultenTheme {
                DashboardScreen(
                    uiState = DashboardUiState(errorMessage = "Error loading data"),
                    onCredentialClick = {},
                    onAddCredentialClick = {},
                    onViewAllClick = {},
                    onRefresh = {},
                    onLogoutClick = {},
                    onSettingsClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(DashboardTestTags.ErrorText).assertIsDisplayed()
        composeRule.onNodeWithTag(DashboardTestTags.RetryButton).assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun dashboard_showsContent() {
        composeRule.setContent {
            VaultenTheme {
                DashboardScreen(
                    uiState = DashboardUiState(dashboardData = sampleData),
                    onCredentialClick = {},
                    onAddCredentialClick = {},
                    onViewAllClick = {},
                    onRefresh = {},
                    onLogoutClick = {},
                    onSettingsClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(DashboardTestTags.Content).assertIsDisplayed()
        composeRule.onNodeWithTag(DashboardTestTags.TotalCredentials).assertIsDisplayed()
    }
}
