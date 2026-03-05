package com.wilove.vaulten.ui.dashboard

import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.model.DashboardData
import com.wilove.vaulten.domain.usecase.GetDashboardDataUseCase
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
    fun `initial state starts as loading and then completes`() = runTest(testDispatcher) {
        // Given
        val mockData = DashboardData(emptyList(), emptyList(), 0)
        coEvery { getDashboardDataUseCase() } returns flowOf(mockData)
        coEvery { getDashboardDataUseCase.sync() } returns Unit

        // When
        viewModel = DashboardViewModel(getDashboardDataUseCase)
        
        // Initial state
        assertTrue(viewModel.uiState.value.isLoading)

        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isLoading)
        assertNotNull(viewModel.uiState.value.dashboardData)
    }

    @Test
    fun `refreshDashboard calls sync on use case`() = runTest(testDispatcher) {
        // Given
        val mockData = DashboardData(emptyList(), emptyList(), 0)
        coEvery { getDashboardDataUseCase() } returns flowOf(mockData)
        coEvery { getDashboardDataUseCase.sync() } returns Unit

        viewModel = DashboardViewModel(getDashboardDataUseCase)
        advanceUntilIdle()

        // When
        viewModel.refreshDashboard()
        advanceUntilIdle()

        // Then
        coVerify { getDashboardDataUseCase.sync() }
    }
}
