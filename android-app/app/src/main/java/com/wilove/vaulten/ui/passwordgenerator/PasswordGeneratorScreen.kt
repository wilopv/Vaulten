package com.wilove.vaulten.ui.passwordgenerator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wilove.vaulten.ui.theme.VaultenTheme

/**
 * Stateless Password Generator UI. Renders purely from [PasswordGeneratorUiState].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordGeneratorScreen(
    uiState: PasswordGeneratorUiState,
    onGenerateClick: () -> Unit,
    onLengthChange: (Int) -> Unit,
    onUppercaseToggle: () -> Unit,
    onLowercaseToggle: () -> Unit,
    onNumbersToggle: () -> Unit,
    onSymbolsToggle: () -> Unit,
    onCopyClick: () -> Unit,
    onBackClick: () -> Unit,
    onUsePasswordClick: () -> Unit = {},
    showUseButton: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Password Generator",
                    modifier = Modifier.testTag(PasswordGeneratorTestTags.Title)
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Error message
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag(PasswordGeneratorTestTags.ErrorMessage)
                )
            }

            // Generated Password Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(PasswordGeneratorTestTags.PasswordCard),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Generated Password",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    SelectionContainer {
                        Text(
                            text = uiState.generatedPassword,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag(PasswordGeneratorTestTags.PasswordText)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Strength indicator
                        val strength = uiState.getPasswordStrength()
                        Text(
                            text = "Strength: ${strength.displayName}",
                            style = MaterialTheme.typography.labelMedium,
                            color = when (strength) {
                                PasswordStrength.WEAK -> MaterialTheme.colorScheme.error
                                PasswordStrength.FAIR -> MaterialTheme.colorScheme.tertiary
                                PasswordStrength.GOOD -> MaterialTheme.colorScheme.secondary
                                PasswordStrength.STRONG -> MaterialTheme.colorScheme.primary
                            }
                        )

                        if (uiState.copiedPassword) {
                            Text(
                                text = "Copied!",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.testTag(PasswordGeneratorTestTags.CopiedIndicator)
                            )
                        } else {
                            IconButton(
                                onClick = onCopyClick,
                                modifier = Modifier.testTag(PasswordGeneratorTestTags.CopyButton)
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy password"
                                )
                            }
                        }
                    }
                }
            }

            // Length Slider
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Length",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "${uiState.length} characters",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag(PasswordGeneratorTestTags.LengthDisplay)
                    )
                }
                Slider(
                    value = uiState.length.toFloat(),
                    onValueChange = { onLengthChange(it.toInt()) },
                    valueRange = 8f..32f,
                    steps = 23,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(PasswordGeneratorTestTags.LengthSlider)
                )
            }

            // Character Options
            Text(
                text = "Character Types",
                style = MaterialTheme.typography.labelMedium
            )

            PasswordOption(
                label = "Uppercase (A-Z)",
                checked = uiState.useUppercase,
                onCheckedChange = { onUppercaseToggle() },
                testTag = PasswordGeneratorTestTags.UppercaseCheckbox
            )

            PasswordOption(
                label = "Lowercase (a-z)",
                checked = uiState.useLowercase,
                onCheckedChange = { onLowercaseToggle() },
                testTag = PasswordGeneratorTestTags.LowercaseCheckbox
            )

            PasswordOption(
                label = "Numbers (0-9)",
                checked = uiState.useNumbers,
                onCheckedChange = { onNumbersToggle() },
                testTag = PasswordGeneratorTestTags.NumbersCheckbox
            )

            PasswordOption(
                label = "Symbols (!@#$%...)",
                checked = uiState.useSymbols,
                onCheckedChange = { onSymbolsToggle() },
                testTag = PasswordGeneratorTestTags.SymbolsCheckbox
            )
        }

        // Generate Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onGenerateClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag(PasswordGeneratorTestTags.GenerateButton)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Generate",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Generate")
            }
            if (showUseButton) {
                Button(
                    onClick = onUsePasswordClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag(PasswordGeneratorTestTags.UseButton)
                ) {
                    Text("Use This")
                }
            }
        }
    }
}

@Composable
private fun PasswordOption(
    label: String,
    checked: Boolean,
    onCheckedChange: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                onValueChange = { onCheckedChange() },
                role = Role.Checkbox
            )
            .padding(vertical = 8.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordGeneratorScreenPreview() {
    VaultenTheme {
        PasswordGeneratorScreen(
            uiState = PasswordGeneratorUiState(
                generatedPassword = "aB3xKp9mQ2wL7nR1vT5kM",
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
}
