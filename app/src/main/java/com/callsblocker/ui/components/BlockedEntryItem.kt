package com.callsblocker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.callsblocker.R
import com.callsblocker.data.BlockedEntry
import com.callsblocker.data.CallAction

@Composable
fun BlockedEntryItem(
    entry: BlockedEntry,
    onDelete: (Long) -> Unit = {},
    onEdit: (BlockedEntry) -> Unit = {},
    onActionChange: (BlockedEntry, CallAction) -> Unit = { _, _ -> }
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = entry.pattern,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (entry.label.isNotEmpty() || entry.isPrefix) {
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (entry.label.isNotEmpty()) {
                            Text(
                                text = entry.label,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (entry.label.isNotEmpty() && entry.isPrefix) {
                            Text(
                                text = " • ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (entry.isPrefix) {
                            Text(
                                text = stringResource(R.string.starts_with),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            val (chipColor, chipLabel) = when (entry.action) {
                CallAction.BLOCK -> MaterialTheme.colorScheme.error to stringResource(R.string.block).uppercase()
                CallAction.SILENCE -> Color(0xFFFF9800) to stringResource(R.string.silence).uppercase()
                CallAction.ALLOW -> Color(0xFF4CAF50) to stringResource(R.string.allow).uppercase()
            }

            SuggestionChip(
                onClick = {
                    val nextAction = when (entry.action) {
                        CallAction.BLOCK -> CallAction.SILENCE
                        CallAction.SILENCE -> CallAction.ALLOW
                        CallAction.ALLOW -> CallAction.BLOCK
                    }
                    onActionChange(entry, nextAction)
                },
                modifier = Modifier.height(28.dp),
                label = {
                    Text(
                        text = chipLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = chipColor.copy(alpha = 0.15f),
                    labelColor = chipColor
                ),
                border = SuggestionChipDefaults.suggestionChipBorder(
                    enabled = true,
                    borderColor = chipColor.copy(alpha = 0.5f),
                    borderWidth = 1.dp
                )
            )

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = { onEdit(entry) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.edit),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }

            IconButton(
                onClick = { onDelete(entry.id) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}
