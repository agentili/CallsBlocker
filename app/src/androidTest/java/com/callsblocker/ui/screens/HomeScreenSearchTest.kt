package com.callsblocker.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.callsblocker.data.BlockedEntry
import com.callsblocker.data.BlockedEntryRepository
import com.callsblocker.data.CallAction
import com.callsblocker.ui.MainViewModel
import com.callsblocker.ui.theme.CallsBlockerTheme
import com.callsblocker.util.PrefsManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenSearchTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_searchFiltering_works() {
        val entries = listOf(
            BlockedEntry(id = 1, pattern = "111", isPrefix = false, label = "Uno", action = CallAction.BLOCK),
            BlockedEntry(id = 2, pattern = "222", isPrefix = false, label = "Due", action = CallAction.BLOCK)
        )
        val repository = mockk<BlockedEntryRepository>(relaxed = true) {
            coEvery { getAll() } returns flowOf(entries)
            coEvery { getAllLogs() } returns flowOf(emptyList())
        }
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val prefsManager = mockk<PrefsManager>(relaxed = true) {
            every { appTheme } returns flowOf("system")
        }
        val viewModel = MainViewModel(repository, prefsManager, context)

        composeTestRule.setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                HomeScreen(viewModel = viewModel, navController = navController)
            }
        }

        // Initially both are visible
        composeTestRule.onNodeWithText("111").assertIsDisplayed()
        composeTestRule.onNodeWithText("222").assertIsDisplayed()

        // Tap search icon
        composeTestRule.onNodeWithContentDescription("Cerca").performClick()

        // Type "Uno" (label of 111)
        composeTestRule.onNodeWithText("Cerca numero o nome...").performTextInput("Uno")

        // Only 111 should be visible
        composeTestRule.onNodeWithText("111").assertIsDisplayed()
        composeTestRule.onNodeWithText("222").assertDoesNotExist()

        // Clear search
        composeTestRule.onNodeWithContentDescription("Cancella").performClick()
        
        // Both back again
        composeTestRule.onNodeWithText("111").assertIsDisplayed()
        composeTestRule.onNodeWithText("222").assertIsDisplayed()
    }
}
