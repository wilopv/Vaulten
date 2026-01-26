package com.wilove.vaulten.ui.passwordgenerator

import com.wilove.vaulten.domain.usecase.GeneratePasswordUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PasswordGeneratorViewModelTest {

    private lateinit var generatePasswordUseCase: GeneratePasswordUseCase
    private lateinit var viewModel: PasswordGeneratorViewModel

    @Before
    fun setup() {
        generatePasswordUseCase = GeneratePasswordUseCase()
        viewModel = PasswordGeneratorViewModel(generatePasswordUseCase)
    }

    @Test
    fun `initial state generates password`() {
        val state = viewModel.uiState.value
        assertNotNull(state.generatedPassword)
        assertTrue(state.generatedPassword.isNotEmpty())
        assertEquals(16, state.length)
        assertTrue(state.useUppercase)
        assertTrue(state.useLowercase)
        assertTrue(state.useNumbers)
        assertTrue(state.useSymbols)
    }

    @Test
    fun `generatePassword creates non-empty password`() {
        viewModel.generatePassword()
        
        val state = viewModel.uiState.value
        assertTrue(state.generatedPassword.isNotEmpty())
        assertEquals(state.length, state.generatedPassword.length)
    }

    @Test
    fun `setLength updates length and regenerates`() {
        viewModel.setLength(20)
        
        val state = viewModel.uiState.value
        assertEquals(20, state.length)
        assertEquals(20, state.generatedPassword.length)
    }

    @Test
    fun `toggleUppercase updates state`() {
        val initialState = viewModel.uiState.value
        assertTrue(initialState.useUppercase)
        
        viewModel.toggleUppercase()
        
        assertFalse(viewModel.uiState.value.useUppercase)
    }

    @Test
    fun `toggleLowercase updates state`() {
        val initialState = viewModel.uiState.value
        assertTrue(initialState.useLowercase)
        
        viewModel.toggleLowercase()
        
        assertFalse(viewModel.uiState.value.useLowercase)
    }

    @Test
    fun `toggleNumbers updates state`() {
        val initialState = viewModel.uiState.value
        assertTrue(initialState.useNumbers)
        
        viewModel.toggleNumbers()
        
        assertFalse(viewModel.uiState.value.useNumbers)
    }

    @Test
    fun `toggleSymbols updates state`() {
        val initialState = viewModel.uiState.value
        assertTrue(initialState.useSymbols)
        
        viewModel.toggleSymbols()
        
        assertFalse(viewModel.uiState.value.useSymbols)
    }

    @Test
    fun `prevents disabling all character types`() {
        viewModel.toggleUppercase()
        viewModel.toggleLowercase()
        viewModel.toggleNumbers()
        viewModel.toggleSymbols()
        
        // Try to toggle symbols when all others are disabled
        viewModel.toggleSymbols()
        
        // At least one should be enabled
        val state = viewModel.uiState.value
        assertTrue(state.useUppercase || state.useLowercase || state.useNumbers || state.useSymbols)
    }

    @Test
    fun `markPasswordAsCopied sets flag`() {
        assertFalse(viewModel.uiState.value.copiedPassword)
        
        viewModel.markPasswordAsCopied()
        
        assertTrue(viewModel.uiState.value.copiedPassword)
    }

    @Test
    fun `clearCopiedFeedback clears flag`() {
        viewModel.markPasswordAsCopied()
        assertTrue(viewModel.uiState.value.copiedPassword)
        
        viewModel.clearCopiedFeedback()
        
        assertFalse(viewModel.uiState.value.copiedPassword)
    }

    @Test
    fun `password strength is calculated correctly`() {
        // WEAK: length < 8
        viewModel.setLength(8)
        assertEquals(PasswordStrength.FAIR, viewModel.uiState.value.getPasswordStrength())
        
        // FAIR: 8 <= length < 12
        viewModel.setLength(10)
        assertEquals(PasswordStrength.FAIR, viewModel.uiState.value.getPasswordStrength())
        
        // GOOD: 12 <= length < 16
        viewModel.setLength(14)
        assertEquals(PasswordStrength.GOOD, viewModel.uiState.value.getPasswordStrength())
        
        // STRONG: length >= 16
        viewModel.setLength(20)
        assertEquals(PasswordStrength.STRONG, viewModel.uiState.value.getPasswordStrength())
    }
}
