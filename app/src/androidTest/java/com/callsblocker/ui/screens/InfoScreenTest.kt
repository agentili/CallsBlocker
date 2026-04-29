package com.callsblocker.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.callsblocker.ui.theme.CallsBlockerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.navigation.compose.rememberNavController

@RunWith(AndroidJUnit4::class)
class InfoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun infoScreen_appName_displayed() {
        composeTestRule.setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                InfoScreen(navController = navController)
            }
        }

        composeTestRule.onNodeWithText("CallsBlocker").assertIsDisplayed()
    }

    @Test
    fun infoScreen_version_displayed() {
        composeTestRule.setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                InfoScreen(navController = navController)
            }
        }

        // Version includes BuildConfig.VERSION_NAME which is 1.0.0
        composeTestRule.onNodeWithText("v1.0.0").assertIsDisplayed()
    }

    @Test
    fun infoScreen_description_displayed() {
        composeTestRule.setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                InfoScreen(navController = navController)
            }
        }

        composeTestRule.onNodeWithText("Descrizione").assertIsDisplayed()
        composeTestRule.onNodeWithText("blacklist personalizzata").assertIsDisplayed()
    }

    @Test
    fun infoScreen_features_displayed() {
        composeTestRule.setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                InfoScreen(navController = navController)
            }
        }

        composeTestRule.onNodeWithText("Funzionalità").assertIsDisplayed()
        composeTestRule.onNodeWithText("Blocco intelligente").assertIsDisplayed()
    }

    @Test
    fun infoScreen_requirements_displayed() {
        composeTestRule.setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                InfoScreen(navController = navController)
            }
        }

        composeTestRule.onNodeWithText("Requisiti").assertIsDisplayed()
        composeTestRule.onNodeWithText("Android 10").assertIsDisplayed()
    }

    @Test
    fun infoScreen_permissions_displayed() {
        composeTestRule.setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                InfoScreen(navController = navController)
            }
        }

        composeTestRule.onNodeWithText("Autorizzazioni").assertIsDisplayed()
        composeTestRule.onNodeWithText("Registro chiamate").assertIsDisplayed()
    }

    @Test
    fun infoScreen_copyright_displayed() {
        composeTestRule.setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                InfoScreen(navController = navController)
            }
        }

        composeTestRule.onNodeWithText("Copyright").assertIsDisplayed()
    }
}
