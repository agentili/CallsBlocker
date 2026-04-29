package com.callsblocker.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.callsblocker.ui.theme.CallsBlockerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun mainActivity_startsAndDisplaysStatusBanner() {
        // Verify app starts and main screen is visible
        composeRule.onNodeWithText("CallsBlocker").assertExists()

        // StatusBanner should be visible
        composeRule.onNodeWithText("Protezione").assertExists()
    }

    @Test
    fun mainActivity_fabOpensAddEntryBottomSheet() {
        // Find and click FAB
        composeRule.onNodeWithContentDescription("Add entry").performClick()

        // Verify bottom sheet opens with input fields
        composeRule.onNodeWithText("Numero o prefisso").assertIsDisplayed()
    }

    @Test
    fun mainActivity_navigationMenu() {
        // Find and click menu button (hamburger icon)
        composeRule.onNodeWithContentDescription("Menu").performClick()

        // Verify menu items are displayed
        composeRule.onNodeWithText("Impostazioni").assertIsDisplayed()
        composeRule.onNodeWithText("Importa CSV o TXT").assertIsDisplayed()
        composeRule.onNodeWithText("Esporta CSV o TXT").assertIsDisplayed()
        composeRule.onNodeWithText("Info").assertIsDisplayed()
    }

    @Test
    fun mainActivity_navigateToSettings() {
        // Open menu
        composeRule.onNodeWithContentDescription("Menu").performClick()

        // Click Settings
        composeRule.onNodeWithText("Impostazioni").performClick()

        // Verify Settings screen is displayed
        composeRule.onNodeWithText("Protezione").assertIsDisplayed()
        composeRule.onNodeWithText("Batteria").assertIsDisplayed()
        composeRule.onNodeWithText("Notifiche").assertIsDisplayed()
    }

    @Test
    fun mainActivity_navigateToInfo() {
        // Open menu
        composeRule.onNodeWithContentDescription("Menu").performClick()

        // Click Info
        composeRule.onNodeWithText("Info").performClick()

        // Verify Info screen is displayed
        composeRule.onNodeWithText("CallsBlocker").assertIsDisplayed()
        composeRule.onNodeWithText("Descrizione").assertExists()
    }

    @Test
    fun mainActivity_backNavigationFromSettings() {
        // Navigate to Settings
        composeRule.onNodeWithContentDescription("Menu").performClick()
        composeRule.onNodeWithText("Impostazioni").performClick()

        // Verify Settings screen
        composeRule.onNodeWithText("Protezione").assertIsDisplayed()

        // Press back (simulated by back button in nav)
        composeRule.onNodeWithContentDescription("Back").performClick()

        // Should return to HomeScreen
        composeRule.onNodeWithContentDescription("Add entry").assertExists()
    }

    @Test
    fun mainActivity_statusBannerDisplaysState() {
        // StatusBanner should be visible on home screen
        composeRule.onNodeWithText("Protezione").assertExists()

        // Should show either "attiva" (if configured) or "non attiva" (if not)
        val bannerExists = try {
            composeRule.onNodeWithText("attiva").assertExists()
            true
        } catch (e: Exception) {
            false
        }

        assert(bannerExists) { "Status banner should display protection state" }
    }
}
