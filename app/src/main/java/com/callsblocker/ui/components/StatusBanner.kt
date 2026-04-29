package com.callsblocker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StatusBanner(
    isScreeningActive: Boolean,
    isBatteryOptimized: Boolean,
    onTapConfigure: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable(enabled = !isScreeningActive, onClick = onTapConfigure),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = when {
                !isScreeningActive -> MaterialTheme.colorScheme.errorContainer
                isBatteryOptimized -> Color(0xFFFFF3E0) // Light orange
                else -> Color(0xFFE8F5E9) // Light green
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when {
                        !isScreeningActive -> Icons.Filled.Warning
                        isBatteryOptimized -> Icons.Filled.Warning
                        else -> Icons.Filled.CheckCircle
                    },
                    contentDescription = null,
                    tint = when {
                        !isScreeningActive -> MaterialTheme.colorScheme.error
                        isBatteryOptimized -> Color(0xFFFF9800)
                        else -> Color(0xFF4CAF50)
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = when {
                        !isScreeningActive -> "Protezione disattivata"
                        isBatteryOptimized -> "Ottimizzazione batteria attiva"
                        else -> "Protezione attiva"
                    },
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = when {
                        !isScreeningActive -> MaterialTheme.colorScheme.onErrorContainer
                        isBatteryOptimized -> Color(0xFFE65100)
                        else -> Color(0xFF2E7D32)
                    }
                )
            }
            
            val subtitle = when {
                !isScreeningActive -> "Tocca per abilitare lo screening chiamate"
                isBatteryOptimized -> "L'app potrebbe non funzionare correttamente in background"
                else -> "Il blocco delle chiamate è funzionante"
            }
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = when {
                    !isScreeningActive -> MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                    isBatteryOptimized -> Color(0xFFE65100).copy(alpha = 0.8f)
                    else -> Color(0xFF2E7D32).copy(alpha = 0.8f)
                },
                modifier = Modifier.padding(top = 4.dp, start = 36.dp)
            )
        }
    }
}
