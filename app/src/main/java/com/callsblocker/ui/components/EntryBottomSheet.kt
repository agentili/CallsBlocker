package com.callsblocker.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.res.stringResource
import com.callsblocker.R
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
    val context = androidx.compose.ui.platform.LocalContext.current
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
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                // Scrollable content with input fields
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp)
                        .padding(top = 16.dp)
                ) {
                    // Pattern field
                    OutlinedTextField(
                        value = pattern,
                        onValueChange = { newValue ->
                            pattern = newValue
                            patternError = validatePattern(context, newValue)
                        },
                        label = { Text(stringResource(R.string.number_or_prefix)) },
                        placeholder = { Text(stringResource(R.string.example_pattern)) },
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
                                text = stringResource(R.string.treat_as_prefix),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = stringResource(R.string.treat_as_prefix_desc),
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
                        text = stringResource(R.string.action_to_take),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val actions = CallAction.entries.toTypedArray()
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
                                            CallAction.BLOCK -> stringResource(R.string.block)
                                            CallAction.SILENCE -> stringResource(R.string.silence)
                                            CallAction.ALLOW -> stringResource(R.string.allow)
                                        }
                                    )
                                }
                            )
                        }
                    }

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
                        Text(stringResource(R.string.cancel))
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
                        Text(if (entryToEdit == null) stringResource(R.string.add) else stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

private fun validatePattern(context: Context, pattern: String): String {
    return when {
        pattern.isEmpty() -> ""
        pattern.length < 4 -> context.getString(R.string.validate_too_short)
        !pattern.all { it.isDigit() || it == '+' } -> context.getString(R.string.validate_invalid_chars)
        else -> ""
    }
}
