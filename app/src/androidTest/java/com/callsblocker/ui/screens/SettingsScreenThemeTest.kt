package com.callsblocker.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.callsblocker.ui.MainViewModel
import com.callsblocker.ui.theme.CallsBlockerTheme
import com.callsblocker.util.AppRoleManager
import com.callsblocker.util.BatteryOptimizationManager
import com.callsblocker.ui.UiState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenThemeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_themeSelection_callsViewModel() {
        val uiStateFlow = MutableStateFlow(UiState(appTheme = "system"))
        val viewModel = mockk<MainViewModel>(relaxed = true) {
            every { uiState } returns uiStateFlow
        }
        val roleManager = mockk<AppRoleManager>(relaxed = true)
        val batteryManager = mockk<BatteryOptimizationManager>(relaxed = true)

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

        // Find "SCURO" button in SegmentedSelector and click it
        composeTestRule.onNodeWithText("SCURO").performClick()

        // Verify viewModel.setAppTheme("dark") was called
        verify { viewModel.setAppTheme("dark") }
    }
}
