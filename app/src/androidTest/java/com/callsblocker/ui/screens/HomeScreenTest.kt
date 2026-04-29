package com.callsblocker.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.callsblocker.data.BlockedEntry
import com.callsblocker.data.BlockedEntryRepository
import com.callsblocker.data.CallAction
import com.callsblocker.ui.MainViewModel
import com.callsblocker.ui.theme.CallsBlockerTheme
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_emptyList_showsEmptyState() {
        val repository = mockk<BlockedEntryRepository> {
            coEvery { getAll() } returns flowOf(emptyList())
        }
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val viewModel = MainViewModel(repository, context)

        composeTestRule.setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                HomeScreen(viewModel = viewModel, navController = navController)
            }
        }

        composeTestRule.onNodeWithText("Nessuna voce nella blacklist").assertIsDisplayed()
    }

    @Test
    fun homeScreen_withEntries_displaysItems() {
        val entry = BlockedEntry(
            id = 1,
            pattern = "+39333",
            isPrefix = true,
            label = "Spam",
            action = CallAction.BLOCK
        )
        val repository = mockk<BlockedEntryRepository> {
            coEvery { getAll() } returns flowOf(listOf(entry))
        }
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val viewModel = MainViewModel(repository, context)

        composeTestRule.setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                HomeScreen(viewModel = viewModel, navController = navController)
            }
        }

        composeTestRule.onNodeWithText("+39333").assertIsDisplayed()
        composeTestRule.onNodeWithText("Spam").assertIsDisplayed()
        composeTestRule.onNodeWithText("BLOCCA").assertIsDisplayed()
    }

    @Test
    fun homeScreen_statusBannerVisible() {
        val repository = mockk<BlockedEntryRepository> {
            coEvery { getAll() } returns flowOf(emptyList())
        }
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val viewModel = MainViewModel(repository, context)

        composeTestRule.setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                HomeScreen(viewModel = viewModel, navController = navController)
            }
        }

        // StatusBanner should be visible (at least one of its states)
        val statusShowing = try {
            composeTestRule.onNodeWithText("Protezione attiva").assertIsDisplayed()
            true
        } catch (e: Exception) {
            try {
                composeTestRule.onNodeWithText("Protezione non attiva").assertIsDisplayed()
                true
            } catch (e: Exception) {
                false
            }
        }
        assert(statusShowing) { "StatusBanner should be displayed" }
    }
}
