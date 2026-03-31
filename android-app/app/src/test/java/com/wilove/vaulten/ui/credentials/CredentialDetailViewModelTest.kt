package com.wilove.vaulten.ui.credentials

import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.usecase.DeleteCredentialUseCase
import com.wilove.vaulten.domain.usecase.GetAllCredentialsUseCase
import com.wilove.vaulten.domain.usecase.GetCredentialByIdUseCase
import com.wilove.vaulten.domain.usecase.PasswordHealthUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
    private lateinit var deleteCredentialUseCase: DeleteCredentialUseCase
    private lateinit var getAllCredentialsUseCase: GetAllCredentialsUseCase
    private val passwordHealthUseCase = PasswordHealthUseCase()
    private lateinit var viewModel: CredentialDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCredentialByIdUseCase = mockk()
        deleteCredentialUseCase = mockk()
        getAllCredentialsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = CredentialDetailViewModel(
        getCredentialByIdUseCase,
        deleteCredentialUseCase,
        getAllCredentialsUseCase,
        passwordHealthUseCase
    )

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
        coEvery { getAllCredentialsUseCase() } returns flowOf(listOf(credential))

        viewModel = buildViewModel()
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

        viewModel = buildViewModel()
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
        coEvery { getAllCredentialsUseCase() } returns flowOf(listOf(credential))

        viewModel = buildViewModel()
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
        coEvery { getAllCredentialsUseCase() } returns flowOf(listOf(credential))

        viewModel = buildViewModel()
        viewModel.loadCredential("1")
        advanceUntilIdle()

        viewModel.markFieldAsCopied("username")
        assertEquals("username", viewModel.uiState.value.copiedField)

        advanceUntilIdle()
        assertEquals(null, viewModel.uiState.value.copiedField)
    }
}
