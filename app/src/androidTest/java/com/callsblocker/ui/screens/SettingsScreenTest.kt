package com.callsblocker.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.callsblocker.ui.theme.CallsBlockerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.navigation.compose.rememberNavController

import com.callsblocker.ui.MainViewModel
import com.callsblocker.ui.UiState
import com.callsblocker.util.AppRoleManager
import com.callsblocker.util.BatteryOptimizationManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun mockViewModel(isActive: Boolean, isOptimized: Boolean): MainViewModel {
        return mockk<MainViewModel>(relaxed = true) {
            every { uiState } returns MutableStateFlow(
                UiState(
                    isScreeningActive = isActive,
                    isBatteryOptimized = isOptimized
                )
            )
        }
    }

    private fun setScreen(
        viewModel: MainViewModel,
        roleManager: AppRoleManager = mockk(relaxed = true),
        batteryManager: BatteryOptimizationManager = mockk(relaxed = true)
    ) {
        composeTestRule.setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                SettingsScreen(
                    navController = navController,
                    viewModel = viewModel,
                    roleManager = roleManager,
                    batteryManager = batteryManager
                )
            }
        }
    }

    @Test
    fun settingsScreen_inactive_showsSetupButton() {
        val viewModel = mockViewModel(isActive = false, isOptimized = false)
        setScreen(viewModel)

        composeTestRule.onNodeWithText("Imposta come app di screening").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_active_showsSuccessMessage() {
        val viewModel = mockViewModel(isActive = true, isOptimized = false)
        setScreen(viewModel)

        composeTestRule.onNodeWithText("App impostata come gestore schermata chiamate")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_active_doesNotShowSetupButton() {
        val viewModel = mockViewModel(isActive = true, isOptimized = false)
        setScreen(viewModel)

        composeTestRule.onNodeWithText("Imposta come app di screening").assertDoesNotExist()
    }

    @Test
    fun settingsScreen_batteryOptimized_showsBatteryButton() {
        val viewModel = mockViewModel(isActive = true, isOptimized = true)
        setScreen(viewModel)

        composeTestRule.onNodeWithText("Gestisci ottimizzazione batteria")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_batteryNotOptimized_showsSuccessMessage() {
        val viewModel = mockViewModel(isActive = true, isOptimized = false)
        setScreen(viewModel)

        composeTestRule.onNodeWithText("Batteria non ottimizzata", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_deleteAllButton_visible() {
        val viewModel = mockViewModel(isActive = true, isOptimized = false)
        setScreen(viewModel)

        composeTestRule.onNodeWithText("Rimuovi tutti i numeri").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_inactive_showsStatusBanner() {
        val viewModel = mockViewModel(isActive = false, isOptimized = false)
        setScreen(viewModel)

        composeTestRule.onNodeWithText("Protezione disattivata").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_allActive_hidesStatusBanner() {
        val viewModel = mockViewModel(isActive = true, isOptimized = false)
        setScreen(viewModel)

        composeTestRule.onNodeWithText("Protezione disattivata").assertDoesNotExist()
        composeTestRule.onNodeWithText("Ottimizzazione batteria attiva").assertDoesNotExist()
    }
}
