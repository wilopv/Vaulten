package com.wilove.vaulten.ui.credentials

import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.usecase.GetAllCredentialsUseCase
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CredentialsListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getAllCredentialsUseCase: GetAllCredentialsUseCase
    private lateinit var viewModel: CredentialsListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getAllCredentialsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCredentials updates state with success`() = runTest(testDispatcher) {
        val credentials = listOf(
            Credential("1", "Gmail", "user@gmail.com", "pass", "https://gmail.com"),
            Credential("2", "GitHub", "dev", "pass", "https://github.com")
        )
        coEvery { getAllCredentialsUseCase() } returns credentials

        viewModel = CredentialsListViewModel(getAllCredentialsUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(credentials, state.credentials)
        assertNull(state.errorMessage)
        coVerify { getAllCredentialsUseCase() }
    }

    @Test
    fun `loadCredentials updates state with error`() = runTest(testDispatcher) {
        coEvery { getAllCredentialsUseCase() } throws Exception("Network error")

        viewModel = CredentialsListViewModel(getAllCredentialsUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorMessage)
        assertEquals(emptyList<Credential>(), state.credentials)
    }

    @Test
    fun `onSearchQueryChange filters credentials`() = runTest(testDispatcher) {
        val credentials = listOf(
            Credential("1", "Gmail", "user@gmail.com", "pass", "https://gmail.com"),
            Credential("2", "GitHub", "dev", "pass", "https://github.com")
        )
        coEvery { getAllCredentialsUseCase() } returns credentials

        viewModel = CredentialsListViewModel(getAllCredentialsUseCase)
        advanceUntilIdle()

        viewModel.onSearchQueryChange("git")

        val state = viewModel.uiState.value
        assertEquals("git", state.searchQuery)
        assertEquals(listOf(credentials[1]), state.credentials)
    }
}
