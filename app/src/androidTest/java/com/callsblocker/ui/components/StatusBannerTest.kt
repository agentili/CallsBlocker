package com.callsblocker.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.callsblocker.ui.theme.CallsBlockerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatusBannerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun statusBanner_active_showsGreenMessage() {
        composeTestRule.setContent {
            CallsBlockerTheme {
                StatusBanner(
                    isScreeningActive = true,
                    isBatteryOptimized = false
                )
            }
        }

        composeTestRule.onNodeWithText("Protezione attiva").assertIsDisplayed()
    }

    @Test
    fun statusBanner_inactive_showsRedMessage() {
        composeTestRule.setContent {
            CallsBlockerTheme {
                StatusBanner(
                    isScreeningActive = false,
                    isBatteryOptimized = false
                )
            }
        }

        composeTestRule.onNodeWithText("Protezione non attiva").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tocca per configurare").assertIsDisplayed()
    }

    @Test
    fun statusBanner_batteryOptimized_showsOrangeWarning() {
        composeTestRule.setContent {
            CallsBlockerTheme {
                StatusBanner(
                    isScreeningActive = true,
                    isBatteryOptimized = true
                )
            }
        }

        composeTestRule.onNodeWithText("Battery optimization attiva").assertIsDisplayed()
    }

    @Test
    fun statusBanner_inactive_clickable() {
        var clicked = false
        composeTestRule.setContent {
            CallsBlockerTheme {
                StatusBanner(
                    isScreeningActive = false,
                    isBatteryOptimized = false,
                    onTapConfigure = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Protezione non attiva").performClick()
        assert(clicked)
    }
}
