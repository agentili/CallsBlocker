package com.callsblocker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
fun EntryBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (BlockedEntry) -> Unit,
    entryToEdit: BlockedEntry? = null,
    showSheet: Boolean = true
) {
    var pattern by remember { mutableStateOf("") }
    var isPrefix by remember { mutableStateOf(false) }
    var selectedAction by remember { mutableStateOf(CallAction.SILENCE) }
    var label by remember { mutableStateOf("") }
    var patternError by remember { mutableStateOf("") }

    LaunchedEffect(entryToEdit) {
        if (entryToEdit != null) {
            pattern = entryToEdit.pattern
            isPrefix = entryToEdit.isPrefix
            selectedAction = entryToEdit.action
            label = entryToEdit.label
        } else {
            pattern = ""
            isPrefix = false
            selectedAction = CallAction.SILENCE
            label = ""
        }
        patternError = ""
    }

    if (showSheet) {
        val scrollState = rememberScrollState()
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Scrollable content with input fields
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp)
                        .padding(top = 16.dp)
                        .imePadding()
                ) {
                    Text(
                        text = if (entryToEdit == null) "Nuova voce blacklist" else "Modifica voce",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Pattern field
                    OutlinedTextField(
                        value = pattern,
                        onValueChange = { newValue ->
                            pattern = newValue
                            patternError = validatePattern(newValue)
                        },
                        label = { Text("Numero o prefisso") },
                        placeholder = { Text("es. +39333123456") },
                        isError = patternError.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )
                    if (patternError.isNotEmpty()) {
                        Text(
                            text = patternError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Prefix switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tratta come prefisso",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Blocca tutti i numeri che iniziano così",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isPrefix,
                            onCheckedChange = { isPrefix = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action selection
                    Text(
                        text = "Azione da intraprendere",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Label field (optional)
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        label = { Text("Nome o Etichetta (opzionale)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Sticky buttons at bottom (always visible)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp)
                        .padding(top = 8.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Annulla")
                    }
                    Button(
                        onClick = {
                            if (patternError.isEmpty()) {
                                val updatedEntry = entryToEdit?.copy(
                                    pattern = pattern,
                                    isPrefix = isPrefix,
                                    label = label,
                                    action = selectedAction
                                ) ?: BlockedEntry(
                                    pattern = pattern,
                                    isPrefix = isPrefix,
                                    label = label,
                                    action = selectedAction
                                )
                                onConfirm(updatedEntry)
                                onDismiss()
                            }
                        },
                        enabled = patternError.isEmpty() && pattern.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(if (entryToEdit == null) "Aggiungi" else "Salva")
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
