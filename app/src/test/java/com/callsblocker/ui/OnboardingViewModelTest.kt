package com.callsblocker.ui

import androidx.test.core.app.ApplicationProvider
import com.callsblocker.util.PrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.runner.RunWith
import android.content.Context

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class OnboardingViewModelTest {

    private lateinit var viewModel: OnboardingViewModel
    private lateinit var context: Context
    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var prefsManager: PrefsManager

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()
        doReturn(flowOf(false)).`when`(prefsManager).onboardingCompleted
        viewModel = OnboardingViewModel(prefsManager, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_step1() = runTest {
        val state = viewModel.uiState.value
        assertEquals(1, state.currentStep)
        assertEquals(false, state.isCompleted)
    }

    @Test
    fun nextStep_incrementsStep() = runTest {
        viewModel.nextStep()
        val state = viewModel.uiState.value
        assertEquals(2, state.currentStep)
    }

    @Test
    fun nextStep_from5_completes() = runTest {
        viewModel.skipToStep(5)
        viewModel.nextStep()
        val state = viewModel.uiState.value
        assertEquals(true, state.isCompleted)
    }

    @Test
    fun skipToStep_jumpsToStep() = runTest {
        viewModel.skipToStep(3)
        val state = viewModel.uiState.value
        assertEquals(3, state.currentStep)
    }

    @Test
    fun skipToStep_invalidStep_ignored() = runTest {
        viewModel.skipToStep(10)
        val state = viewModel.uiState.value
        assertEquals(1, state.currentStep) // Should stay at 1
    }

    @Test
    fun completeOnboarding_setsCompleted() = runTest {
        viewModel.completeOnboarding()
        val state = viewModel.uiState.value
        assertEquals(true, state.isCompleted)
    }

    /**
     * Regression test: completeOnboarding() MUST persist the flag in PrefsManager.
     * Bug history: Step5Completed used to call onCompleted() directly, never invoking
     * completeOnboarding(), which caused an infinite onboarding loop because
     * MainActivity always read onboardingCompleted=false.
     */
    @Test
    fun completeOnboarding_persistsFlagToPrefsManager() = runTest {
        viewModel.completeOnboarding()
        runBlocking { verify(prefsManager).setOnboardingCompleted(true) }
    }

    /**
     * Regression test: integration with real PrefsManager — verify the flag is
     * actually written to DataStore when completeOnboarding() is called.
     */
    @Test
    fun completeOnboarding_realPrefsManager_writesToDataStore() = runTest {
        val realPrefsManager = PrefsManager(context)
        runBlocking { realPrefsManager.setOnboardingCompleted(false) }
        val realViewModel = OnboardingViewModel(realPrefsManager, context)

        realViewModel.completeOnboarding()

        // Wait briefly for the viewModelScope coroutine to complete
        kotlinx.coroutines.delay(100)

        val isCompleted = realPrefsManager.onboardingCompleted.first()
        assertEquals(true, isCompleted)
    }
}
