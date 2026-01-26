package com.wilove.vaulten.ui.credentials

import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.usecase.CreateCredentialUseCase
import com.wilove.vaulten.domain.usecase.GetCredentialByIdUseCase
import com.wilove.vaulten.domain.usecase.UpdateCredentialUseCase
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
class CreateEditCredentialViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var createCredentialUseCase: CreateCredentialUseCase
    private lateinit var updateCredentialUseCase: UpdateCredentialUseCase
    private lateinit var getCredentialByIdUseCase: GetCredentialByIdUseCase
    private lateinit var viewModel: CreateEditCredentialViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        createCredentialUseCase = mockk()
        updateCredentialUseCase = mockk()
        getCredentialByIdUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onNameChange updates state`() = runTest(testDispatcher) {
        viewModel = CreateEditCredentialViewModel(
            createCredentialUseCase,
            updateCredentialUseCase,
            getCredentialByIdUseCase
        )

        viewModel.onNameChange("Gmail")

        assertEquals("Gmail", viewModel.uiState.value.name)
        assertNull(viewModel.uiState.value.nameError)
    }

    @Test
    fun `onUsernameChange updates state`() = runTest(testDispatcher) {
        viewModel = CreateEditCredentialViewModel(
            createCredentialUseCase,
            updateCredentialUseCase,
            getCredentialByIdUseCase
        )

        viewModel.onUsernameChange("user@gmail.com")

        assertEquals("user@gmail.com", viewModel.uiState.value.username)
        assertNull(viewModel.uiState.value.usernameError)
    }

    @Test
    fun `onPasswordChange updates state`() = runTest(testDispatcher) {
        viewModel = CreateEditCredentialViewModel(
            createCredentialUseCase,
            updateCredentialUseCase,
            getCredentialByIdUseCase
        )

        viewModel.onPasswordChange("securePassword123")

        assertEquals("securePassword123", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.passwordError)
    }

    @Test
    fun `onUrlChange updates state`() = runTest(testDispatcher) {
        viewModel = CreateEditCredentialViewModel(
            createCredentialUseCase,
            updateCredentialUseCase,
            getCredentialByIdUseCase
        )

        viewModel.onUrlChange("https://gmail.com")

        assertEquals("https://gmail.com", viewModel.uiState.value.url)
    }

    @Test
    fun `saveCredential validates required fields`() = runTest(testDispatcher) {
        viewModel = CreateEditCredentialViewModel(
            createCredentialUseCase,
            updateCredentialUseCase,
            getCredentialByIdUseCase
        )

        viewModel.saveCredential()

        val state = viewModel.uiState.value
        assertNotNull(state.nameError)
        assertNotNull(state.usernameError)
        assertNotNull(state.passwordError)
    }

    @Test
    fun `saveCredential creates new credential`() = runTest(testDispatcher) {
        coEvery { createCredentialUseCase(any()) } returns Unit

        viewModel = CreateEditCredentialViewModel(
            createCredentialUseCase,
            updateCredentialUseCase,
            getCredentialByIdUseCase
        )

        viewModel.onNameChange("Gmail")
        viewModel.onUsernameChange("user@gmail.com")
        viewModel.onPasswordChange("password123")
        viewModel.saveCredential()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.savedSuccessfully)
        assertFalse(viewModel.uiState.value.isSaving)
        coVerify { createCredentialUseCase(any()) }
    }

    @Test
    fun `saveCredential updates existing credential`() = runTest(testDispatcher) {
        val credential = Credential(
            id = "1",
            name = "Gmail",
            username = "user@gmail.com",
            password = "password123",
            url = "https://gmail.com"
        )
        coEvery { getCredentialByIdUseCase("1") } returns credential
        coEvery { updateCredentialUseCase(any()) } returns Unit

        viewModel = CreateEditCredentialViewModel(
            createCredentialUseCase,
            updateCredentialUseCase,
            getCredentialByIdUseCase
        )

        // Load credential for editing
        viewModel.loadCredentialForEditing("1")
        advanceUntilIdle()

        viewModel.onNameChange("Gmail Updated")
        viewModel.onUsernameChange("updated@gmail.com")
        viewModel.onPasswordChange("newPassword123")
        viewModel.saveCredential()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.savedSuccessfully)
        assertTrue(viewModel.uiState.value.isEditMode)
        coVerify { updateCredentialUseCase(any()) }
    }

    @Test
    fun `loadCredentialForEditing loads credential for editing`() = runTest(testDispatcher) {
        val credential = Credential(
            id = "1",
            name = "Gmail",
            username = "user@gmail.com",
            password = "password123",
            url = "https://gmail.com"
        )
        coEvery { getCredentialByIdUseCase("1") } returns credential

        viewModel = CreateEditCredentialViewModel(
            createCredentialUseCase,
            updateCredentialUseCase,
            getCredentialByIdUseCase
        )

        viewModel.loadCredentialForEditing("1")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Gmail", state.name)
        assertEquals("user@gmail.com", state.username)
        assertEquals("password123", state.password)
        assertEquals("https://gmail.com", state.url)
        assertTrue(state.isEditMode)
    }

    @Test
    fun `saveCredential shows error on failure`() = runTest(testDispatcher) {
        coEvery { createCredentialUseCase(any()) } throws Exception("Save failed")

        viewModel = CreateEditCredentialViewModel(
            createCredentialUseCase,
            updateCredentialUseCase,
            getCredentialByIdUseCase
        )

        viewModel.onNameChange("Gmail")
        viewModel.onUsernameChange("user@gmail.com")
        viewModel.onPasswordChange("password123")
        viewModel.saveCredential()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.errorMessage)
        assertFalse(state.savedSuccessfully)
    }
}
