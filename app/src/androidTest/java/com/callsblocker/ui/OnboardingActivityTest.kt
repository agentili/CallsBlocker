package com.callsblocker.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.callsblocker.ui.theme.CallsBlockerTheme
import com.callsblocker.util.AppRoleManager
import com.callsblocker.util.BatteryOptimizationManager
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onboarding_step1_displayed() {
        val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<OnboardingViewModel>()
        val roleManager = mockk<AppRoleManager>()
        val batteryManager = mockk<BatteryOptimizationManager>()

        composeTestRule.setContent {
            CallsBlockerTheme {
                OnboardingContent(
                    uiState = viewModel.uiState.value,
                    viewModel = viewModel,
                    roleManager = roleManager,
                    batteryManager = batteryManager,
                    onCompleted = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Benvenuto in CallsBlocker").assertIsDisplayed()
        composeTestRule.onNodeWithText("Avanti").assertIsDisplayed()
    }

    @Test
    fun onboarding_step1_toStep2() {
        val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<OnboardingViewModel>()
        val roleManager = mockk<AppRoleManager>()
        val batteryManager = mockk<BatteryOptimizationManager>()

        composeTestRule.setContent {
            CallsBlockerTheme {
                OnboardingContent(
                    uiState = viewModel.uiState.value,
                    viewModel = viewModel,
                    roleManager = roleManager,
                    batteryManager = batteryManager,
                    onCompleted = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Avanti").performClick()

        // Verify moved to step 2
        composeTestRule.onNodeWithText("Accesso al registro chiamate").assertIsDisplayed()
    }

    @Test
    fun onboarding_step5_completion() {
        val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<OnboardingViewModel>()
        viewModel.skipToStep(5)

        val roleManager = mockk<AppRoleManager>()
        val batteryManager = mockk<BatteryOptimizationManager>()

        composeTestRule.setContent {
            CallsBlockerTheme {
                OnboardingContent(
                    uiState = viewModel.uiState.value,
                    viewModel = viewModel,
                    roleManager = roleManager,
                    batteryManager = batteryManager,
                    onCompleted = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Configurazione completata!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Accedi all'app").assertIsDisplayed()
    }

    @Test
    fun onboarding_allStepsHaveNavigation() {
        val steps = listOf(
            Pair(1, "Benvenuto in CallsBlocker"),
            Pair(2, "Accesso al registro chiamate"),
            Pair(3, "Impostazione come app di screening"),
            Pair(4, "Esenzione da ottimizzazione batteria"),
            Pair(5, "Configurazione completata!")
        )

        steps.forEach { (step, _) ->
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<OnboardingViewModel>()
            viewModel.skipToStep(step)

            val roleManager = mockk<AppRoleManager>()
            val batteryManager = mockk<BatteryOptimizationManager>()

            composeTestRule.setContent {
                CallsBlockerTheme {
                    OnboardingContent(
                        uiState = viewModel.uiState.value,
                        viewModel = viewModel,
                        roleManager = roleManager,
                        batteryManager = batteryManager,
                        onCompleted = {}
                    )
                }
            }

            // Each step should have a navigation button
            val buttons = listOf("Avanti", "Concedi accesso", "Imposta come gestore", "Configura batteria", "Accedi all'app", "Salta")
            var found = false
            buttons.forEach { button ->
                try {
                    composeTestRule.onNodeWithText(button).assertIsDisplayed()
                    found = true
                } catch (e: Exception) {
                    // Button not in this step
                }
            }
            assert(found) { "Step $step should have a navigation button" }
        }
    }
}
