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
class ExportImportScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun exportImport_showsCoreElements() {
        composeRule.setContent {
            VaultenTheme {
                ExportImportScreen(
                    uiState = ExportImportUiState(),
                    onExportCsvClick = {},
                    onRequestEncryptedExportClick = {},
                    onExportEncryptedConfirm = {},
                    onExportEncryptedDismiss = {},
                    onImportEncryptedConfirm = {},
                    onImportEncryptedDismiss = {},
                    onFileSelected = {},
                    onCsvExportHandled = {},
                    onJsonExportHandled = {},
                    onBackClick = {},
                    onClearMessages = {}
                )
            }
        }

        composeRule.onNodeWithTag(ExportImportTestTags.ExportCsvButton).assertIsDisplayed().assertIsEnabled()
        composeRule.onNodeWithTag(ExportImportTestTags.ExportJsonButton).assertIsDisplayed().assertIsEnabled()
        composeRule.onNodeWithTag(ExportImportTestTags.ImportButton).assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun exportImport_showsLoadingState() {
        composeRule.setContent {
            VaultenTheme {
                ExportImportScreen(
                    uiState = ExportImportUiState(isLoading = true),
                    onExportCsvClick = {},
                    onRequestEncryptedExportClick = {},
                    onExportEncryptedConfirm = {},
                    onExportEncryptedDismiss = {},
                    onImportEncryptedConfirm = {},
                    onImportEncryptedDismiss = {},
                    onFileSelected = {},
                    onCsvExportHandled = {},
                    onJsonExportHandled = {},
                    onBackClick = {},
                    onClearMessages = {}
                )
            }
        }

        composeRule.onNodeWithTag(ExportImportTestTags.Loading).assertIsDisplayed()
    }
}
