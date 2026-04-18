package com.wilove.vaulten.ui.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wilove.vaulten.ui.theme.VaultenTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun settings_showsCoreElements() {
        composeRule.setContent {
            VaultenTheme {
                SettingsScreen(
                    uiState = SettingsUiState(),
                    onBackClick = {},
                    onEnableAutofillClick = {},
                    onCheckStatus = {},
                    onChangePasswordClick = {},
                    onExportImportClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(SettingsTestTags.AutofillButton).assertExists().assertIsEnabled()
        composeRule.onNodeWithTag(SettingsTestTags.ChangePasswordButton).assertExists().assertIsEnabled()
        composeRule.onNodeWithTag(SettingsTestTags.ExportImportButton).assertExists().assertIsEnabled()
    }

    @Test
    fun settings_autofillEnabled_showsActiveStatus() {
        composeRule.setContent {
            VaultenTheme {
                SettingsScreen(
                    uiState = SettingsUiState(isAutofillEnabled = true),
                    onBackClick = {},
                    onEnableAutofillClick = {},
                    onCheckStatus = {},
                    onChangePasswordClick = {},
                    onExportImportClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(SettingsTestTags.AutofillButton).assertIsDisplayed()
    }
}
