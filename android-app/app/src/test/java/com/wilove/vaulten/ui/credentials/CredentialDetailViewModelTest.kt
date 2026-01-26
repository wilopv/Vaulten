package com.wilove.vaulten.ui.credentials

import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.usecase.GetCredentialByIdUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CredentialDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getCredentialByIdUseCase: GetCredentialByIdUseCase
    private lateinit var viewModel: CredentialDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCredentialByIdUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCredential updates state with success`() = runTest(testDispatcher) {
        val credential = Credential(
            id = "1",
            name = "Gmail",
            username = "user@gmail.com",
            password = "pass123",
            url = "https://gmail.com"
        )
        coEvery { getCredentialByIdUseCase("1") } returns credential

        viewModel = CredentialDetailViewModel(getCredentialByIdUseCase)
        viewModel.loadCredential("1")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.credential)
        assertEquals(credential, state.credential)
        assertNull(state.errorMessage)
        coVerify { getCredentialByIdUseCase("1") }
    }

    @Test
    fun `loadCredential updates state with error`() = runTest(testDispatcher) {
        coEvery { getCredentialByIdUseCase("invalid") } throws Exception("Credential not found")

        viewModel = CredentialDetailViewModel(getCredentialByIdUseCase)
        viewModel.loadCredential("invalid")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorMessage)
        assertNull(state.credential)
    }

    @Test
    fun `togglePasswordVisibility toggles state`() = runTest(testDispatcher) {
        val credential = Credential(
            id = "1",
            name = "Gmail",
            username = "user@gmail.com",
            password = "pass123",
            url = "https://gmail.com"
        )
        coEvery { getCredentialByIdUseCase("1") } returns credential

        viewModel = CredentialDetailViewModel(getCredentialByIdUseCase)
        viewModel.loadCredential("1")
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.passwordVisible)
        viewModel.togglePasswordVisibility()
        assertTrue(viewModel.uiState.value.passwordVisible)
        viewModel.togglePasswordVisibility()
        assertFalse(viewModel.uiState.value.passwordVisible)
    }

    @Test
    fun `markFieldAsCopied sets copiedField`() = runTest(testDispatcher) {
        val credential = Credential(
            id = "1",
            name = "Gmail",
            username = "user@gmail.com",
            password = "pass123",
            url = "https://gmail.com"
        )
        coEvery { getCredentialByIdUseCase("1") } returns credential

        viewModel = CredentialDetailViewModel(getCredentialByIdUseCase)
        viewModel.loadCredential("1")
        advanceUntilIdle()

        viewModel.markFieldAsCopied("username")
        assertEquals("username", viewModel.uiState.value.copiedField)

        // Advance time to clear the copied field
        advanceUntilIdle()
        assertEquals(null, viewModel.uiState.value.copiedField)
    }
}
