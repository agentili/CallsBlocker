package com.callsblocker.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.rememberNavController

@RunWith(AndroidJUnit4::class)
class HomeScreenAddEntryTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_fabClick_opensBottomSheet() {
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

        composeTestRule.onNodeWithText("Aggiungi").performClick()

        composeTestRule.onNodeWithText("Aggiungi voce alla blacklist").assertIsDisplayed()
    }

    @Test
    fun homeScreen_addEntry_appearInList() {
        var addedEntry: BlockedEntry? = null
        val repository = mockk<BlockedEntryRepository> {
            coEvery { getAll() } returns flowOf(emptyList())
            coEvery { insert(any()) } answers {
                addedEntry = firstArg()
            }
        }
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val viewModel = MainViewModel(repository, context)

        composeTestRule.setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                HomeScreen(viewModel = viewModel, navController = navController)
            }
        }

        // Open bottom sheet
        composeTestRule.onNodeWithText("Aggiungi").performClick()

        // Fill in form
        composeTestRule.onNodeWithText("Numero (es. +39333123456 o 39333)")
            .performTextInput("+39333")
        composeTestRule.onNodeWithText("Etichetta (opzionale)")
            .performTextInput("Test entry")

        // Submit
        composeTestRule.onNodeWithText("Aggiungi").performClick()

        // Verify entry was added
        assert(addedEntry != null)
        assert(addedEntry?.pattern == "+39333")
    }
}
