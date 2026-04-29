package com.callsblocker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import com.callsblocker.data.BlockedEntry
import com.callsblocker.data.CallAction

import androidx.compose.material.icons.filled.Edit

@Composable
fun BlockedEntryItem(
    entry: BlockedEntry,
    onDelete: (Long) -> Unit = {},
    onEdit: (BlockedEntry) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                                text = "Inizia con",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            val (chipColor, chipLabel) = when (entry.action) {
                CallAction.BLOCK -> MaterialTheme.colorScheme.error to "BLOCCA"
                CallAction.SILENCE -> Color(0xFFFF9800) to "SILENZIA"
                CallAction.ALLOW -> Color(0xFF4CAF50) to "CONSENTI"
            }

            SuggestionChip(
                onClick = {},
                label = {
                    Text(
                        text = chipLabel,
                        style = MaterialTheme.typography.labelSmall,
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
                    contentDescription = "Modifica",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }

            IconButton(
                onClick = { onDelete(entry.id) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Elimina",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}
