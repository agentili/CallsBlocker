# Edit Blocked Entry Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Enable users to edit existing blocked numbers with precompiled data in a modal sheet, apply changes immediately to the database, and remove the colored action chip from entry cards.

**Architecture:** Reuse `AddEntryBottomSheet` in dual-mode (add/edit) based on an optional `editingEntry` parameter. Add an Edit button to `BlockedEntryItem` with corresponding callback. Add `updateEntry()` method to `MainViewModel` to persist changes. Manage edit state locally in `HomeScreen` via `remember`.

**Tech Stack:** Kotlin 2.1, Jetpack Compose, Room 2.7, Coroutines, Material3

---

## File Structure

**Files to modify:**
- `app/src/main/java/com/callsblocker/ui/components/AddEntryBottomSheet.kt` — Add `editingEntry` param, modal title/button logic, precompilation
- `app/src/main/java/com/callsblocker/ui/components/BlockedEntryItem.kt` — Add Edit IconButton, remove action chip
- `app/src/main/java/com/callsblocker/ui/MainViewModel.kt` — Add `updateEntry()` method
- `app/src/main/java/com/callsblocker/ui/screens/HomeScreen.kt` — Add `editingEntry` state, callbacks

**Files to create (tests):**
- `app/src/test/java/com/callsblocker/ui/MainViewModelUpdateTest.kt` — Test `updateEntry()`
- `app/src/androidTest/java/com/callsblocker/ui/AddEntryBottomSheetEditTest.kt` — Test edit mode

---

## Tasks

### Task 1: Add updateEntry() to MainViewModel

**Files:**
- Modify: `app/src/main/java/com/callsblocker/ui/MainViewModel.kt:90-92`
- Test: `app/src/test/java/com/callsblocker/ui/MainViewModelUpdateTest.kt` (new)

- [ ] **Step 1: Write the failing test**

Create `app/src/test/java/com/callsblocker/ui/MainViewModelUpdateTest.kt`:

```kotlin
package com.callsblocker.ui

import androidx.lifecycle.viewModelScope
import com.callsblocker.data.BlockedEntry
import com.callsblocker.data.BlockedEntryRepository
import com.callsblocker.data.CallAction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MainViewModelUpdateTest {
    private lateinit var repository: BlockedEntryRepository
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        viewModel = MainViewModel(repository, mockk(relaxed = true))
    }

    @Test
    fun updateEntry_callsRepositoryUpdate() = runTest {
        val entry = BlockedEntry(
            id = 1,
            pattern = "39333123456",
            isPrefix = false,
            label = "Test",
            action = CallAction.BLOCK
        )

        viewModel.updateEntry(entry)
        
        // Give coroutine time to execute
        advanceUntilIdle()
        
        coVerify { repository.update(entry) }
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

```bash
cd C:\Users\New\git\CallsBlocker
.\gradlew.bat test --tests "com.callsblocker.ui.MainViewModelUpdateTest" -v
```

Expected: FAIL with "Unresolved reference 'updateEntry'"

- [ ] **Step 3: Write minimal implementation**

Modify `app/src/main/java/com/callsblocker/ui/MainViewModel.kt`. Add after `deleteEntry()` method (around line 90):

```kotlin
fun updateEntry(entry: BlockedEntry) {
    viewModelScope.launch {
        repository.update(entry)
    }
}
```

Complete method context:
```kotlin
fun deleteEntry(entryId: Long) {
    viewModelScope.launch {
        val entryToDelete = _uiState.value.entries.find { it.id == entryId } ?: return@launch
        repository.delete(entryToDelete)
    }
}

fun updateEntry(entry: BlockedEntry) {
    viewModelScope.launch {
        repository.update(entry)
    }
}

fun exportCsv(outputStream: OutputStream) {
```

- [ ] **Step 4: Run test to verify it passes**

```bash
cd C:\Users\New\git\CallsBlocker
.\gradlew.bat test --tests "com.callsblocker.ui.MainViewModelUpdateTest" -v
```

Expected: PASS

- [ ] **Step 5: Commit**

```bash
cd C:\Users\New\git\CallsBlocker
git add app/src/main/java/com/callsblocker/ui/MainViewModel.kt app/src/test/java/com/callsblocker/ui/MainViewModelUpdateTest.kt
git commit -m "feat: add updateEntry method to MainViewModel"
```

---

### Task 2: Modify AddEntryBottomSheet to Support Edit Mode

**Files:**
- Modify: `app/src/main/java/com/callsblocker/ui/components/AddEntryBottomSheet.kt`
- Test: `app/src/androidTest/java/com/callsblocker/ui/AddEntryBottomSheetEditTest.kt` (new)

- [ ] **Step 1: Write the failing test**

Create `app/src/androidTest/java/com/callsblocker/ui/AddEntryBottomSheetEditTest.kt`:

```kotlin
package com.callsblocker.ui

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.callsblocker.data.BlockedEntry
import com.callsblocker.data.CallAction
import com.callsblocker.ui.components.AddEntryBottomSheet
import org.junit.Rule
import org.junit.Test

class AddEntryBottomSheetEditTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun editMode_displaysEditTitle() {
        val entry = BlockedEntry(
            id = 1,
            pattern = "39333123456",
            isPrefix = false,
            label = "Spam",
            action = CallAction.BLOCK
        )

        composeTestRule.setContent {
            AddEntryBottomSheet(
                showSheet = true,
                editingEntry = entry,
                onDismiss = {},
                onSaveEntry = {}
            )
        }

        composeTestRule
            .onNodeWithText("Modifica voce")
            .assert(hasText("Modifica voce"))
    }

    @Test
    fun editMode_displaysPrefilledPattern() {
        val entry = BlockedEntry(
            id = 1,
            pattern = "39333123456",
            isPrefix = false,
            label = "Spam",
            action = CallAction.BLOCK
        )

        composeTestRule.setContent {
            AddEntryBottomSheet(
                showSheet = true,
                editingEntry = entry,
                onDismiss = {},
                onSaveEntry = {}
            )
        }

        composeTestRule
            .onNodeWithText("39333123456")
            .assert(hasText("39333123456"))
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

```bash
cd C:\Users\New\git\CallsBlocker
.\gradlew.bat connectedAndroidTest --tests "com.callsblocker.ui.AddEntryBottomSheetEditTest" -v
```

Expected: FAIL (connectedAndroidTest requires emulator/device, or test will fail on "editingEntry" parameter not found)

- [ ] **Step 3: Modify AddEntryBottomSheet signature and implementation**

Replace entire `AddEntryBottomSheet.kt` with:

```kotlin
package com.callsblocker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.callsblocker.data.BlockedEntry
import com.callsblocker.data.CallAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryBottomSheet(
    onDismiss: () -> Unit,
    onSaveEntry: (BlockedEntry) -> Unit,
    showSheet: Boolean = true,
    editingEntry: BlockedEntry? = null
) {
    val isEditMode = editingEntry != null
    
    var pattern by remember { mutableStateOf(editingEntry?.pattern ?: "") }
    var isPrefix by remember { mutableStateOf(editingEntry?.isPrefix ?: false) }
    var selectedAction by remember { mutableStateOf(editingEntry?.action ?: CallAction.BLOCK) }
    var label by remember { mutableStateOf(editingEntry?.label ?: "") }
    var patternError by remember { mutableStateOf("") }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (isEditMode) "Modifica voce" else "Aggiungi voce alla blacklist",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Pattern field
                OutlinedTextField(
                    value = pattern,
                    onValueChange = { newValue ->
                        pattern = newValue
                        patternError = validatePattern(newValue)
                    },
                    label = { Text("Numero (es. +39333123456 o 39333)") },
                    isError = patternError.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                if (patternError.isNotEmpty()) {
                    Text(
                        text = patternError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Prefix switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        text = "Prefisso (blocca tutti i numeri che iniziano così)",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isPrefix,
                        onCheckedChange = { isPrefix = it }
                    )
                }

                // Action selection
                Text(
                    text = "Azione",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    val actions = CallAction.values()
                    actions.forEachIndexed { index, action ->
                        SegmentedButton(
                            selected = selectedAction == action,
                            onClick = { selectedAction = action },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = actions.size
                            ),
                            label = {
                                Text(
                                    when (action) {
                                        CallAction.BLOCK -> "Blocca"
                                        CallAction.SILENCE -> "Silenzia"
                                        CallAction.ALLOW -> "Consenti"
                                    }
                                )
                            }
                        )
                    }
                }

                // Label field (optional)
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Etichetta (opzionale)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annulla")
                    }
                    Button(
                        onClick = {
                            if (patternError.isEmpty()) {
                                val entryToSave = if (isEditMode) {
                                    editingEntry!!.copy(
                                        pattern = pattern,
                                        isPrefix = isPrefix,
                                        label = label,
                                        action = selectedAction
                                    )
                                } else {
                                    BlockedEntry(
                                        pattern = pattern,
                                        isPrefix = isPrefix,
                                        label = label,
                                        action = selectedAction
                                    )
                                }
                                onSaveEntry(entryToSave)
                                onDismiss()
                            }
                        },
                        enabled = patternError.isEmpty() && pattern.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isEditMode) "Salva" else "Aggiungi")
                    }
                }
            }
        }
    }
}

private fun validatePattern(pattern: String): String {
    return when {
        pattern.isEmpty() -> ""
        pattern.length < 4 -> "Il numero deve avere almeno 4 caratteri"
        !pattern.all { it.isDigit() || it == '+' } -> "Solo numeri e '+' sono permessi"
        else -> ""
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

```bash
cd C:\Users\New\git\CallsBlocker
.\gradlew.bat test -v
```

Expected: All existing tests still pass (unit tests for components)

- [ ] **Step 5: Commit**

```bash
cd C:\Users\New\git\CallsBlocker
git add app/src/main/java/com/callsblocker/ui/components/AddEntryBottomSheet.kt app/src/androidTest/java/com/callsblocker/ui/AddEntryBottomSheetEditTest.kt
git commit -m "feat: add edit mode to AddEntryBottomSheet with precompiled data"
```

---

### Task 3: Add Edit Button to BlockedEntryItem and Remove Action Chip

**Files:**
- Modify: `app/src/main/java/com/callsblocker/ui/components/BlockedEntryItem.kt`

- [ ] **Step 1: Update function signature**

Replace entire `BlockedEntryItem.kt` with:

```kotlin
package com.callsblocker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.callsblocker.data.BlockedEntry

@Composable
fun BlockedEntryItem(
    entry: BlockedEntry,
    onDelete: (Long) -> Unit = {},
    onEdit: (BlockedEntry) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = entry.pattern,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (entry.label.isNotEmpty()) {
                    Text(
                        text = entry.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (entry.isPrefix) {
                    Text(
                        text = "(prefisso)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            IconButton(
                onClick = { onEdit(entry) },
                modifier = Modifier.padding(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Modifica",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { onDelete(entry.id) },
                modifier = Modifier.padding(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Elimina",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

- [ ] **Step 2: Verify no breaking changes to existing calls**

```bash
cd C:\Users\New\git\CallsBlocker
.\gradlew.bat compileDebugKotlin
```

Expected: No errors (default `onEdit = {}` maintains backward compatibility)

- [ ] **Step 3: Commit**

```bash
cd C:\Users\New\git\CallsBlocker
git add app/src/main/java/com/callsblocker/ui/components/BlockedEntryItem.kt
git commit -m "feat: add edit button to BlockedEntryItem and remove action chip"
```

---

### Task 4: Update HomeScreen to Add Edit State and Callbacks

**Files:**
- Modify: `app/src/main/java/com/callsblocker/ui/screens/HomeScreen.kt`

- [ ] **Step 1: Locate the HomeScreen function**

Open `app/src/main/java/com/callsblocker/ui/screens/HomeScreen.kt` and find the main `@Composable fun HomeScreen()`. Note where the `AddEntryBottomSheet` and list of `BlockedEntryItem` are rendered.

- [ ] **Step 2: Add editingEntry state at the top of HomeScreen**

Add after any existing state declarations (typically after any `var` declarations for sheet visibility):

```kotlin
var editingEntry by remember { mutableStateOf<BlockedEntry?>(null) }
```

Example location context:
```kotlin
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    val uiState = viewModel.uiState.collectAsState().value
    var showAddSheet by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<BlockedEntry?>(null) }
    
    // ... rest of composable
}
```

- [ ] **Step 3: Update AddEntryBottomSheet call**

Find the existing `AddEntryBottomSheet(...)` call and replace it with:

```kotlin
AddEntryBottomSheet(
    showSheet = showAddSheet || editingEntry != null,
    editingEntry = editingEntry,
    onDismiss = {
        showAddSheet = false
        editingEntry = null
    },
    onSaveEntry = { entry ->
        if (editingEntry != null) {
            viewModel.updateEntry(entry)
        } else {
            viewModel.addEntry(entry)
        }
        showAddSheet = false
        editingEntry = null
    }
)
```

**Context:** Replace only the `AddEntryBottomSheet(...)` invocation, keeping all surrounding code intact.

- [ ] **Step 4: Update BlockedEntryItem calls in the list**

Find where `BlockedEntryItem(...)` is called (typically in a LazyColumn or similar). Add the `onEdit` callback:

```kotlin
BlockedEntryItem(
    entry = entry,
    onEdit = { selectedEntry -> editingEntry = selectedEntry },
    onDelete = { entryId -> viewModel.deleteEntry(entryId) }
)
```

**Context:** The exact location depends on how the list is structured. It could be in a `LazyColumn`, `Column`, or custom list composable. The key is to pass `onEdit = { ... }` to every `BlockedEntryItem` call.

Example full context:
```kotlin
LazyColumn(
    modifier = Modifier
        .fillMaxSize()
        .padding(top = 12.dp)
) {
    items(uiState.entries, key = { it.id }) { entry ->
        BlockedEntryItem(
            entry = entry,
            onEdit = { selectedEntry -> editingEntry = selectedEntry },
            onDelete = { entryId -> viewModel.deleteEntry(entryId) }
        )
    }
}
```

- [ ] **Step 5: Verify compilation**

```bash
cd C:\Users\New\git\CallsBlocker
.\gradlew.bat compileDebugKotlin -v
```

Expected: No compilation errors

- [ ] **Step 6: Build and test manually**

```bash
cd C:\Users\New\git\CallsBlocker
.\gradlew.bat assembleDebug
```

Expected: BUILD SUCCESSFUL

Then install on emulator/device:
```bash
.\gradlew.bat installDebug
```

**Manual testing steps:**
1. Open app and add a number (e.g., "39333123456")
2. In the list, tap the Edit button (pencil icon) on the entry
3. Verify the sheet opens with precompiled data (number, label, action, prefix toggle)
4. Change the number to "39333999999" and tap "Salva"
5. Verify the list updates immediately with the new number
6. Verify the colored action chip is NOT visible on the entry card
7. Tap Edit again to verify data is still precompiled correctly

- [ ] **Step 7: Commit**

```bash
cd C:\Users\New\git\CallsBlocker
git add app/src/main/java/com/callsblocker/ui/screens/HomeScreen.kt
git commit -m "feat: add edit state and callbacks to HomeScreen"
```

---

## Summary

Four focused tasks implement the complete edit feature:

1. **Task 1:** Add `updateEntry()` to ViewModel (with unit test)
2. **Task 2:** Modify `AddEntryBottomSheet` to dual-mode add/edit (with UI test)
3. **Task 3:** Add Edit button to `BlockedEntryItem`, remove action chip
4. **Task 4:** Integrate edit state into `HomeScreen`, wire callbacks

All changes follow TDD (tests first), commit frequently, and maintain backward compatibility.
