package com.callsblocker.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.callsblocker.data.BlockedEntry
import com.callsblocker.data.CallAction
import com.callsblocker.ui.theme.CallsBlockerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EntryBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun entrySheet_validPattern_submitEnabled() {
        composeTestRule.setContent {
            CallsBlockerTheme {
                EntryBottomSheet(
                    onDismiss = {},
                    onConfirm = {},
                    showSheet = true
                )
            }
        }

        composeTestRule.onNodeWithText("Numero o prefisso")
            .performTextInput("+39333123456")

        composeTestRule.onNodeWithText("Aggiungi").assertIsEnabled()
    }

    @Test
    fun entrySheet_shortPattern_submitDisabled() {
        composeTestRule.setContent {
            CallsBlockerTheme {
                EntryBottomSheet(
                    onDismiss = {},
                    onConfirm = {},
                    showSheet = true
                )
            }
        }

        composeTestRule.onNodeWithText("Numero o prefisso")
            .performTextInput("123")

        composeTestRule.onNodeWithText("Il numero deve avere almeno 4 caratteri").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aggiungi").assertIsNotEnabled()
    }

    @Test
    fun entrySheet_invalidCharacters_showsError() {
        composeTestRule.setContent {
            CallsBlockerTheme {
                EntryBottomSheet(
                    onDismiss = {},
                    onConfirm = {},
                    showSheet = true
                )
            }
        }

        composeTestRule.onNodeWithText("Numero o prefisso")
            .performTextInput("39abc1234")

        composeTestRule.onNodeWithText("Solo numeri e '+' sono permessi").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aggiungi").assertIsNotEnabled()
    }

    @Test
    fun entrySheet_validInput_submitCalls() {
        var confirmedEntry: BlockedEntry? = null
        var dismissed = false

        composeTestRule.setContent {
            CallsBlockerTheme {
                EntryBottomSheet(
                    onDismiss = { dismissed = true },
                    onConfirm = { confirmedEntry = it },
                    showSheet = true
                )
            }
        }

        composeTestRule.onNodeWithText("Numero o prefisso")
            .performTextInput("+39333")
        composeTestRule.onNodeWithText("Nome o Etichetta (opzionale)")
            .performTextInput("Spam test")

        composeTestRule.onNodeWithText("Aggiungi").performClick()

        assert(confirmedEntry != null)
        assert(confirmedEntry?.pattern == "+39333")
        assert(confirmedEntry?.label == "Spam test")
        assert(confirmedEntry?.action == CallAction.BLOCK)
        assert(dismissed)
    }

    @Test
    fun entrySheet_editMode_prefillsData() {
        val entry = BlockedEntry(id = 10, pattern = "555123", isPrefix = true, label = "Test Edit", action = CallAction.SILENCE)
        var confirmedEntry: BlockedEntry? = null

        composeTestRule.setContent {
            CallsBlockerTheme {
                EntryBottomSheet(
                    onDismiss = {},
                    onConfirm = { confirmedEntry = it },
                    entryToEdit = entry,
                    showSheet = true
                )
            }
        }

        composeTestRule.onNodeWithText("Modifica voce").assertIsDisplayed()
        composeTestRule.onNodeWithText("555123").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Edit").assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Salva").performClick()
        
        assert(confirmedEntry?.id == 10L)
        assert(confirmedEntry?.pattern == "555123")
    }

    @Test
    fun entrySheet_buttonsAreAlwaysVisible() {
        composeTestRule.setContent {
            CallsBlockerTheme {
                EntryBottomSheet(
                    onDismiss = {},
                    onConfirm = {},
                    showSheet = true
                )
            }
        }

        // Both buttons should be displayed immediately without any scrolling
        composeTestRule.onNodeWithText("Annulla").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aggiungi").assertIsDisplayed()
    }

    @Test
    fun entrySheet_buttonsRemainVisibleWithInput() {
        composeTestRule.setContent {
            CallsBlockerTheme {
                EntryBottomSheet(
                    onDismiss = {},
                    onConfirm = {},
                    showSheet = true
                )
            }
        }

        // Input text and verify buttons are still visible
        composeTestRule.onNodeWithText("Numero o prefisso")
            .performTextInput("+39333123456")
        composeTestRule.onNodeWithText("Nome o Etichetta (opzionale)")
            .performTextInput("Test Label")

        // Buttons must remain visible even with filled input fields
        composeTestRule.onNodeWithText("Annulla").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aggiungi").assertIsDisplayed()
    }

    @Test
    fun entrySheet_buttonIsClickableAfterInput() {
        var confirmedEntry: BlockedEntry? = null

        composeTestRule.setContent {
            CallsBlockerTheme {
                EntryBottomSheet(
                    onDismiss = {},
                    onConfirm = { confirmedEntry = it },
                    showSheet = true
                )
            }
        }

        // Fill in valid pattern to enable button
        composeTestRule.onNodeWithText("Numero o prefisso")
            .performTextInput("+39333123456")

        // Verify the button is clickable
        composeTestRule.onNodeWithText("Aggiungi").assertIsEnabled()
        
        // Perform click and verify callback is invoked
        composeTestRule.onNodeWithText("Aggiungi").performClick()
        assert(confirmedEntry != null)
    }
}
