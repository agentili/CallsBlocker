package com.callsblocker.ui

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.callsblocker.data.BlockedEntryRepository
import com.callsblocker.ui.theme.CallsBlockerTheme
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.callsblocker.ui.screens.HomeScreen
import com.callsblocker.ui.screens.SettingsScreen
import com.callsblocker.ui.screens.InfoScreen
import androidx.compose.ui.test.onNodeWithContentDescription

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun navigation_settingsMenu_navigatesToSettings() {
        val repository = mockk<BlockedEntryRepository> {
            coEvery { getAll() } returns flowOf(emptyList())
            coEvery { getAllLogs() } returns flowOf(emptyList())
        }
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()

        composeTestRule.setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                val viewModel = MainViewModel(repository, context)

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(viewModel = viewModel, navController = navController)
                    }
                    composable("settings") {
                        SettingsScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                    composable("info") {
                        InfoScreen(navController = navController)
                    }
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithText("Impostazioni").performClick()
        composeTestRule.onNodeWithText("Impostazioni").assertIsDisplayed()
    }

    @Test
    fun navigation_infoMenu_navigatesToInfo() {
        val repository = mockk<BlockedEntryRepository> {
            coEvery { getAll() } returns flowOf(emptyList())
            coEvery { getAllLogs() } returns flowOf(emptyList())
        }
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()

        composeTestRule.setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                val viewModel = MainViewModel(repository, context)

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(viewModel = viewModel, navController = navController)
                    }
                    composable("settings") {
                        SettingsScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                    composable("info") {
                        InfoScreen(navController = navController)
                    }
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithText("Info").performClick()
        composeTestRule.onNodeWithText("Info").assertIsDisplayed()
    }

    @Test
    fun navigation_allMenuItems_visible() {
        val repository = mockk<BlockedEntryRepository> {
            coEvery { getAll() } returns flowOf(emptyList())
            coEvery { getAllLogs() } returns flowOf(emptyList())
        }
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()

        composeTestRule.setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                val viewModel = MainViewModel(repository, context)

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(viewModel = viewModel, navController = navController)
                    }
                    composable("settings") {
                        SettingsScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                    composable("info") {
                        InfoScreen(navController = navController)
                    }
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithText("Impostazioni").assertIsDisplayed()
        composeTestRule.onNodeWithText("Importa CSV/TXT").assertIsDisplayed()
        composeTestRule.onNodeWithText("Esporta CSV/TXT").assertIsDisplayed()
        composeTestRule.onNodeWithText("Info").assertIsDisplayed()
    }
}
