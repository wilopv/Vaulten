package com.wilove.vaulten.ui.credentials

import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.model.CredentialFilter
import com.wilove.vaulten.domain.usecase.GetAllCredentialsUseCase
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CredentialsListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getAllCredentialsUseCase: GetAllCredentialsUseCase
    private val passwordHealthUseCase = PasswordHealthUseCase()
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
        coEvery { getAllCredentialsUseCase() } returns flowOf(credentials)

        viewModel = CredentialsListViewModel(getAllCredentialsUseCase, passwordHealthUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(credentials, state.credentials)
        assertNull(state.errorMessage)
        coVerify { getAllCredentialsUseCase() }
    }

    @Test
    fun `onSearchQueryChange filters credentials`() = runTest(testDispatcher) {
        val credentials = listOf(
            Credential("1", "Gmail", "user@gmail.com", "pass", "https://gmail.com"),
            Credential("2", "GitHub", "dev", "pass", "https://github.com")
        )
        coEvery { getAllCredentialsUseCase() } returns flowOf(credentials)

        viewModel = CredentialsListViewModel(getAllCredentialsUseCase, passwordHealthUseCase)
        advanceUntilIdle()

        viewModel.onSearchQueryChange("git")

        val state = viewModel.uiState.value
        assertEquals("git", state.searchQuery)
        assertEquals(listOf(credentials[1]), state.credentials)
    }

    @Test
    fun `applyFilter with domain filters by url`() = runTest(testDispatcher) {
        val credentials = listOf(
            Credential("1", "Gmail", "user@gmail.com", "pass", "https://gmail.com"),
            Credential("2", "GitHub", "dev", "pass", "https://github.com")
        )
        coEvery { getAllCredentialsUseCase() } returns flowOf(credentials)
        viewModel = CredentialsListViewModel(getAllCredentialsUseCase, passwordHealthUseCase)
        advanceUntilIdle()

        viewModel.applyFilter(CredentialFilter(domain = "github"))

        assertEquals(listOf(credentials[1]), viewModel.uiState.value.credentials)
        assertTrue(viewModel.uiState.value.isFilterActive)
    }

    @Test
    fun `applyFilter with modifiedAfter excludes older credentials`() = runTest(testDispatcher) {
        val now = System.currentTimeMillis()
        val credentials = listOf(
            Credential("1", "Old", "u", "p", lastModified = now - 10_000),
            Credential("2", "New", "u", "p", lastModified = now)
        )
        coEvery { getAllCredentialsUseCase() } returns flowOf(credentials)
        viewModel = CredentialsListViewModel(getAllCredentialsUseCase, passwordHealthUseCase)
        advanceUntilIdle()

        viewModel.applyFilter(CredentialFilter(modifiedAfter = now - 1_000))

        assertEquals(listOf(credentials[1]), viewModel.uiState.value.credentials)
    }

    @Test
    fun `applyFilter combines with search query using AND`() = runTest(testDispatcher) {
        val credentials = listOf(
            Credential("1", "Gmail", "user@gmail.com", "pass", "https://gmail.com"),
            Credential("2", "GitHub", "dev", "pass", "https://github.com"),
            Credential("3", "GitLab", "dev", "pass", "https://gitlab.com")
        )
        coEvery { getAllCredentialsUseCase() } returns flowOf(credentials)
        viewModel = CredentialsListViewModel(getAllCredentialsUseCase, passwordHealthUseCase)
        advanceUntilIdle()

        viewModel.onSearchQueryChange("git")
        viewModel.applyFilter(CredentialFilter(domain = "github"))

        // Must match both "git" in name AND "github" in url
        assertEquals(listOf(credentials[1]), viewModel.uiState.value.credentials)
    }

    @Test
    fun `clearFilter restores full list filtered only by search query`() = runTest(testDispatcher) {
        val credentials = listOf(
            Credential("1", "Gmail", "user@gmail.com", "pass", "https://gmail.com"),
            Credential("2", "GitHub", "dev", "pass", "https://github.com")
        )
        coEvery { getAllCredentialsUseCase() } returns flowOf(credentials)
        viewModel = CredentialsListViewModel(getAllCredentialsUseCase, passwordHealthUseCase)
        advanceUntilIdle()

        viewModel.applyFilter(CredentialFilter(domain = "github"))
        assertEquals(1, viewModel.uiState.value.credentials.size)

        viewModel.clearFilter()

        assertFalse(viewModel.uiState.value.isFilterActive)
        assertEquals(credentials, viewModel.uiState.value.credentials)
    }

    @Test
    fun `credential without url is excluded when domain filter is active`() = runTest(testDispatcher) {
        val credentials = listOf(
            Credential("1", "NoUrl", "u", "p", url = null),
            Credential("2", "GitHub", "dev", "pass", "https://github.com")
        )
        coEvery { getAllCredentialsUseCase() } returns flowOf(credentials)
        viewModel = CredentialsListViewModel(getAllCredentialsUseCase, passwordHealthUseCase)
        advanceUntilIdle()

        viewModel.applyFilter(CredentialFilter(domain = "github"))

        assertEquals(listOf(credentials[1]), viewModel.uiState.value.credentials)
    }
}
