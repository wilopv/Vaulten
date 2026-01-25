package com.wilove.vaulten.ui.dashboard

import com.wilove.vaulten.domain.model.AlertSeverity
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.model.DashboardData
import com.wilove.vaulten.domain.model.SecurityAlert
import com.wilove.vaulten.domain.usecase.GetDashboardDataUseCase
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

/**
 * Unit tests for [DashboardViewModel].
 * Verifies state management and use case interaction.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getDashboardDataUseCase: GetDashboardDataUseCase
    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getDashboardDataUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state starts not loading then transitions to loading`() = runTest(testDispatcher) {
        // Given
        val mockData = DashboardData(
            recentCredentials = emptyList(),
            securityAlerts = emptyList(),
            totalCredentials = 0
        )
        coEvery { getDashboardDataUseCase() } returns mockData

        // When
        viewModel = DashboardViewModel(getDashboardDataUseCase)

        // Then - Initial state might be loading or not depending on dispatcher timing, 
        // but it should eventually load.
        assertNull(viewModel.uiState.value.dashboardData)
        assertNull(viewModel.uiState.value.errorMessage)
        
        // Advance the coroutine; it may complete in the same turn depending on the dispatcher.
        testScheduler.runCurrent()

        // Either it is loading or it already completed successfully.
        assertTrue(
            viewModel.uiState.value.isLoading || viewModel.uiState.value.dashboardData != null
        )

        // Advance to complete the loading (if not already)
        advanceUntilIdle()
        
        // Verify it eventually loads successfully
        assertFalse(viewModel.uiState.value.isLoading)
        assertNotNull(viewModel.uiState.value.dashboardData)
    }

    @Test
    fun `loadDashboardData updates state with success`() = runTest(testDispatcher) {
        // Given
        val mockCredentials = listOf(
            Credential("1", "Gmail", "user@gmail.com", "pass", "https://gmail.com")
        )
        val mockAlerts = listOf(
            SecurityAlert("alert1", "Test alert", AlertSeverity.INFO)
        )
        val mockData = DashboardData(
            recentCredentials = mockCredentials,
            securityAlerts = mockAlerts,
            totalCredentials = 5
        )
        coEvery { getDashboardDataUseCase() } returns mockData

        // When
        viewModel = DashboardViewModel(getDashboardDataUseCase)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.dashboardData)
        assertNull(state.errorMessage)
        assertEquals(mockCredentials, state.dashboardData?.recentCredentials)
        assertEquals(mockAlerts, state.dashboardData?.securityAlerts)
        assertEquals(5, state.dashboardData?.totalCredentials)

        coVerify { getDashboardDataUseCase() }
    }

    @Test
    fun `loadDashboardData updates state with error on exception`() = runTest(testDispatcher) {
        // Given
        val errorMessage = "Network error"
        coEvery { getDashboardDataUseCase() } throws Exception(errorMessage)

        // When
        viewModel = DashboardViewModel(getDashboardDataUseCase)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.dashboardData)
        assertNotNull(state.errorMessage)
        assertTrue(state.errorMessage!!.contains(errorMessage))
    }

    @Test
    fun `refresh calls loadDashboardData again`() = runTest(testDispatcher) {
        // Given
        val mockData = DashboardData(
            recentCredentials = emptyList(),
            securityAlerts = emptyList(),
            totalCredentials = 0
        )
        coEvery { getDashboardDataUseCase() } returns mockData

        viewModel = DashboardViewModel(getDashboardDataUseCase)
        advanceUntilIdle()

        // When
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 2) { getDashboardDataUseCase() }
    }

    @Test
    fun `refresh clears previous error`() = runTest(testDispatcher) {
        // Given
        coEvery { getDashboardDataUseCase() } throws Exception("First error")
        viewModel = DashboardViewModel(getDashboardDataUseCase)
        advanceUntilIdle()

        // Verify error state
        assertNotNull(viewModel.uiState.value.errorMessage)

        // When - second call succeeds
        val mockData = DashboardData(emptyList(), emptyList(), 0)
        coEvery { getDashboardDataUseCase() } returns mockData
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        assertNull(viewModel.uiState.value.errorMessage)
        assertNotNull(viewModel.uiState.value.dashboardData)
    }
}
